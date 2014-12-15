
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
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.config.DatabaseConfig;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class Database implements AutoCloseable
{
    private final static String DRIVER = "org.mariadb.jdbc.Driver";

    private final static String DRIVER_FORMAT = "jdbc:mariadb://%s/%s";

    private final Map<String/*database*/, ObjectProperty<Connection>> connections = new HashMap<>();

    public Database()
    {
        for (final DatabaseType type : DatabaseType.values())
            if (type.isRequired())
                connections.put(type.getId(), new SimpleObjectProperty<Connection>());

        // Try connect after the config was updated
        WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_LOADED, this)
        {
            @Override
            public void informed()
            {
                try
                {
                    // Try to load our driver
                    Class.forName(DRIVER);
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

    private static String getConnectionString(final String host, final String database)
    {
        return String.format(DRIVER_FORMAT, host, database); // Database
    }

    private void connect()
    {
        final List<DatabaseConfig> dbList = WIde.getConfig().get().getActiveEnviroment().getDatabases();
        for (final DatabaseConfig config : dbList)
        {
            try
            {
                final Connection con = DriverManager.getConnection(
                        getConnectionString(config.host().get(), config.name().get()),
                            config.user().get(), config.password().get());

                ObjectProperty<Connection> property = connections.get(config.id().get());
                if (property == null)
                {
                    property = new SimpleObjectProperty<>();
                    connections.put(config.id().get(), property);
                }

                property.set(con);
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
                close();
                return;
            }

            if (WIde.getEnviroment().isTraceEnabled())
                System.out.println(String.format("Database Type %s loaded: %s", config.id().get(), config.name().get()));
        }

        /*
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
    */
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

    public ReadOnlyObjectProperty<Connection> connection(final String type)
    {
        return connections.get(type);
    }
}
