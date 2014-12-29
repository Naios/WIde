package com.github.naios.wide.database_pool.internal;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.configuration.ConfigService;
import com.github.naios.wide.api.configuration.main.DatabaseConfig;
import com.github.naios.wide.api.database_pool.Database;
import com.github.naios.wide.api.database_pool.DatabaseNotRegisteredException;
import com.github.naios.wide.api.database_pool.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    private ConfigService config;

    /**
     * The default jdbc driver to initialize
     */
    private final static String DEFAULT_DRIVER = "org.mariadb.jdbc.Driver";

    /**
     * Its possible to set this property to replace the default driver.
     */
    private final static String CUSTOM_DRIVER_PROPERTY = "com.github.naios.wide.database_pool.custom_driver";

    /**
     * The default driver format to create connection strings
     */
    private final static String DRIVER_FORMAT = "jdbc:mysql://%s/%s"; // 1: Host (ip:port), 2. Database

    private final Map<String /*id*/, ObjectProperty<DatabaseImpl> /*database*/> connections =
            new HashMap<>();

    private final ChangeListener<String> listener = new ChangeListener<String>()
    {
        @Override
        public void changed(final ObservableValue<? extends String> observable,
                final String oldValue, final String newValue)
        {
            final Set<String> remove = new HashSet<>();
            connections.forEach((id, database) ->
            {
                if (database.get().isOptional())
                {
                    database.get().close();
                    database.set(null);
                    remove.add(id);
                }
                else
                    database.set(createDatabase(config.getActiveEnviroment().getDatabaseConfig(id)));
            });

            remove.forEach(id -> connections.remove(id));
        }
    };

    public void open()
    {
        // Try to load our preferred jdbc driver
        final String name = System.getProperty(CUSTOM_DRIVER_PROPERTY, DEFAULT_DRIVER);
        try
        {
            Class.forName(name);
        }
        catch (final Throwable e)
        {
            System.out.println(String.format("DEBUG: Didn't find jdbc class %s", name));
        }

        for (final DatabaseConfig dbconfig : config.getActiveEnviroment().getDatabases())
            connections.put(dbconfig.id().get(), new SimpleObjectProperty<>(createDatabase(dbconfig)));

        System.out.println(String.format("DEBUG: %s", "DatabasePoolService::open()"));
    }

    public void close()
    {
        connections.forEach((id, database) ->
        {
            database.get().close();
            database.set(null);
        });
        System.out.println(String.format("DEBUG: %s", "DatabasePoolService::close()"));
    }

    public void setConfig(final ConfigService config)
    {
        this.config = config;
        this.config.activeEnviroment().addListener(listener);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public synchronized ObjectProperty<Database> requestConnection(final String id)
            throws DatabaseNotRegisteredException
    {
        final ObjectProperty<DatabaseImpl> database = connections.get(id);
        if (Objects.isNull(database))
            throw new DatabaseNotRegisteredException(id);
        else
            return (ObjectProperty)database;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public synchronized ObjectProperty<Database> registerConnection(final String id,
            final String endpoint, final String user, final String password, final String table)
            throws SQLException
    {
        if (connections.containsKey(id))
            return (ObjectProperty)connections.get(id);

        return registerDatabase(id, endpoint, user, password, table, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SimpleObjectProperty<Database> registerDatabase(final String id,
            final String endpoint, final String user, final String password,
            final String table, final boolean optional) throws SQLException
    {
        final SimpleObjectProperty<DatabaseImpl> database = new SimpleObjectProperty<DatabaseImpl>(
                createDatabase(id, endpoint, user, password, table, optional));
        connections.put(id, database);
        return (SimpleObjectProperty)database;
    }

    private DatabaseImpl createDatabase(final DatabaseConfig config)
    {
        return createDatabase(config.id().get(), config.host().get(),
                config.user().get(), config.password().get(), config.name().get(), false);
    }

    private DatabaseImpl createDatabase(final String id,
            final String host, final String user, final String password,
            final String table, final boolean optional)
    {
        final String connection = String.format(DRIVER_FORMAT, host, table);
        return new DatabaseImpl(connection, user, password, id, table, optional);
    }
}
