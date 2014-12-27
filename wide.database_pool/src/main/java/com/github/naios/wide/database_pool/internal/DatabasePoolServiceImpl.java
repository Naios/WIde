package com.github.naios.wide.database_pool.internal;

import java.sql.SQLException;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

import com.github.naios.wide.configuration.ConfigService;
import com.github.naios.wide.configuration.DatabaseConfig;
import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.DatabaseNotRegisteredException;
import com.github.naios.wide.database_pool.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    private ConfigService config;

    private final StringProperty currentEnviroment =
            new SimpleStringProperty();

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
    private final static String DEFAULT_DRIVER_FORMAT = "jdbc:mariadb://%s/%s";

    /**
     * Its possible to set this property to replace the default driver connection format string.
     */
    private final static String CUSTOM_DRIVER_FORMAT_PROPERTY = "com.github.naios.wide.database_pool.custom_driver_format";

    private final ObservableMap<String /*id*/, ObjectProperty<DatabaseImpl> /*database*/> connections =
            new SimpleMapProperty<>();

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

        currentEnviroment.bind(config.activeEnviroment());
        currentEnviroment.addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(final ObservableValue<? extends String> observable,
                    final String oldValue, final String newValue)
            {
                connections.forEach((id, database) ->
                {
                    if (!id.equals(newValue))
                    {
                        database.get().close();

                        if (database.get().isOptional())
                            connections.remove(id);
                        else
                        {
                            try
                            {
                                database.set(createDatabase(config.getActiveEnviroment().getDatabaseConfig(id)));
                            }
                            catch (final SQLException e)
                            {
                            }
                        }
                    }
                });
            }
        });



        System.out.println(String.format("DEBUG: %s", "DatabasePoolService::open()"));
    }

    public void close()
    {
        connections.forEach((id, database) ->
        {
            connections.remove(id);
            database.get().close();
        });

        System.out.println(String.format("DEBUG: %s", "DatabasePoolService::close()"));
    }

    public void setConfig(final ConfigService config)
    {
        this.config = config;
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

    private DatabaseImpl createDatabase(final DatabaseConfig config) throws SQLException
    {
        return createDatabase(config.id().get(), config.endpoint().get(),
                config.user().get(), config.password().get(), config.name().get(), false);
    }

    private DatabaseImpl createDatabase(final String id,
            final String endpoint, final String user, final String password,
            final String table, final boolean optional) throws SQLException
    {
        final String connection = String.format(System.getProperty(CUSTOM_DRIVER_FORMAT_PROPERTY, DEFAULT_DRIVER_FORMAT), table);
        return new DatabaseImpl(connection, user, password, id, table, optional);
    }
}
