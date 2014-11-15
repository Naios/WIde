package wide.core.session.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wide.core.WIde;
import wide.core.session.config.ConfigEntry;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;

public class Database
{
    private Map<DatabaseType, Connection> connections = new HashMap<>();

    private static String GetConnectionStringForDatabase(String db)
    {
        return "jdbc:mysql://" + WIde.getConfig().getProperty(ConfigEntry.CONFIG_DATABASE_HOST.getStorageName()).get() + ":"
                + WIde.getConfig().getProperty(ConfigEntry.CONFIG_DATABASE_PORT.getStorageName()).get() + "/" + db + "?" + "user="
                + WIde.getConfig().getProperty(ConfigEntry.CONFIG_DATABASE_USER.getStorageName()).get() + "&" + "password="
                + WIde.getConfig().getProperty(ConfigEntry.CONFIG_DATABASE_PASSWORD.getStorageName()).get();
    }

    public Database()
    {
        // Try connect after the config was updated
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
            final String con_string = GetConnectionStringForDatabase(
                    WIde.getConfig().getProperty(type.getConfigEntry().getStorageName()).get());

            try
            {
                final Connection connection = DriverManager.getConnection(con_string);
                connections.put(type, connection);

            } catch (final SQLException e)
            {
                close();
                return;
            }
        }

        // Hook.ON_DATABASE_ESTABLISHED
        WIde.getHooks().fire(Hook.ON_DATABASE_ESTABLISHED);
    }

    private void close()
    {
        final Collection<Connection> con_list = connections.values();
        for (final Connection con : con_list)
            try
            {
                con.close();

            } catch (final SQLException e)
            {
            }

        connections.clear();

        // Hook.ON_DATABASE_CLOSE
        WIde.getHooks().fire(Hook.ON_DATABASE_CLOSE);
    }

    public Connection getConnection(DatabaseType type)
    {
        return connections.get(type);
    }
}
