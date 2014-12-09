
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class Database implements AutoCloseable
{
    private final Map<DatabaseType, ObjectProperty<Connection>> connections = new HashMap<>();

    public Database()
    {
        for (final DatabaseType type : DatabaseType.values())
            connections.put(type, new SimpleObjectProperty<Connection>());

        // Try connect after the config was updated
        WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_LOADED, this)
        {
            @Override
            public void informed()
            {
                try
                {
                    // Try to load our driver
                    Class.forName(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_DRIVER).get());
                } catch (final ClassNotFoundException e)
                {
                }

                if (!isConnected())
                    connect();
            }
        });

        WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_CHANGED, this)
        {
            @Override
            public void informed()
            {
                if (!isConnected())
                    connect();
            }
        });

        // Close all Connections at end
        WIde.getHooks().addListener(new HookListener(Hook.ON_APPLICATION_STOP, this)
        {
            @Override
            public void informed()
            {
                close();
            }
        });
    }

    public boolean isConnected()
    {
        for (final ObjectProperty<Connection> connection : connections.values())
            try
            {
                if (connection.get() == null)
                    return false;

                if (connection.get().isClosed())
                    return false;
            }
            catch (final SQLException e)
            {
                return false;
            }

        return true;
    }

    private String getConnectionString(final String database)
    {
        return String.format(WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_DRIVER_STRING).get(),
                WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST).get(), // Host
                    WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PORT).get(), // Port
                        database); // Database
    }

    private void connect()
    {
        for (final Entry<DatabaseType, ObjectProperty<Connection>> connection : connections.entrySet())
        {
            final String name = WIde.getConfig().getProperty(connection.getKey().getConfigEntry()).get();

            try
            {
                connection.getValue().set(DriverManager.getConnection(
                        getConnectionString(name), // Connection String
                            WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER).get(), // User
                                WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PASSWORD).get())); // Password
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
                close();
                return;
            }

            if (WIde.getEnviroment().isTraceEnabled())
                System.out.println(String.format("Database Type %s loaded: %s", connection.getKey(), name));
        }

        // Hook.ON_DATABASE_ESTABLISHED
        WIde.getHooks().fire(Hook.ON_DATABASE_ESTABLISHED);
    }

    @Override
    public void close()
    {
        // Hook.ON_DATABASE_CLOSE
        WIde.getHooks().fire(Hook.ON_DATABASE_CLOSE);

        for (final ObjectProperty<Connection> connection : connections.values())
            try
            {
                if (connection.get() != null)
                    connection.get().close();

            }
            catch (final SQLException e)
            {
            }
    }

    public ReadOnlyObjectProperty<Connection> connection(final DatabaseType type)
    {
        return connections.get(type);
    }
}
