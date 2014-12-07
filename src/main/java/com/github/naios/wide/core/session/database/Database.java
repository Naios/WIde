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

    private static String getConnectionStringForDatabase(final String db)
    {
        return "jdbc:mysql://" + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST).get() + ":"
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PORT).get() + "/" + db + "?" + "user="
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER).get() + "&" + "password="
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PASSWORD).get()
                + "&allowMultiQueries=true"
                + "&autoReConnect=true";
    }

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

    private void connect()
    {
        for (final Entry<DatabaseType, ObjectProperty<Connection>> connection : connections.entrySet())
        {
            final String connectionString = getConnectionStringForDatabase(
                    WIde.getConfig().getProperty(connection.getKey().getConfigEntry()).get());

            try
            {
                connection.getValue().set(DriverManager.getConnection(connectionString));
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
                close();
                return;
            }
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
