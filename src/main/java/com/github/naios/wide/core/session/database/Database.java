package com.github.naios.wide.core.session.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class Database implements AutoCloseable
{
    private final Map<DatabaseType, Connection> connections = new HashMap<>();

    private static String getConnectionStringForDatabase(final String db)
    {
        return "jdbc:mysql://" + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_HOST).get() + ":"
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PORT).get() + "/" + db + "?" + "user="
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_USER).get() + "&" + "password="
                + WIde.getConfig().getProperty(Constants.PROPERTY_DATABASE_PASSWORD).get();
    }

    public Database()
    {
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
        if (connections.size() != DatabaseType.values().length)
            return false;

        final Collection<Connection> con_list = connections.values();
        for (final Connection con : con_list)
            try
            {
                if (con.isClosed())
                    return false;

            } catch (final SQLException e)
            {
                return false;
            }

        return true;
    }

    private void connect()
    {
        for (final DatabaseType type : DatabaseType.values())
        {
            final String con_string = getConnectionStringForDatabase(
                    WIde.getConfig().getProperty(type.getConfigEntry()).get());

            try
            {
                final Connection connection = DriverManager.getConnection(con_string);
                connections.put(type, connection);

            } catch (final SQLException e)
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
        WIde.getHooks().fire(Hook.ON_DATABASE_CLOSED);

        final Collection<Connection> con_list = connections.values();
        for (final Connection con : con_list)
            try
            {
                con.close();

            } catch (final SQLException e)
            {
            }

        connections.clear();
    }

    public Connection getConnection(final DatabaseType type)
    {
        return connections.get(type);
    }
}
