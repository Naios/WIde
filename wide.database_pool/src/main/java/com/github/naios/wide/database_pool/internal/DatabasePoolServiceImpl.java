package com.github.naios.wide.database_pool.internal;

import java.sql.SQLException;
import java.util.Objects;

import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

import com.github.naios.wide.configuration.ConfigService;
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

    private final ObservableMap<String /*id*/, DatabaseImpl /*database*/> connections =
            new SimpleMapProperty<>();

    public void open()
    {
        // Try to load our preferred jdbc driver
        /*
        try
        {
            Class.forName(System.getProperty(CUSTOM_DRIVER_PROPERTY, DEFAULT_DRIVER));
        }
        catch (final throwable e)
        {
            e.printStackTrace();
        }*/

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
                        connections.remove(id);
                        database.close();
                    }
                    else
                        database.update();
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
            database.close();
        });

        System.out.println(String.format("DEBUG: %s", "DatabasePoolService::close()"));
    }

    public void setConfig(final ConfigService config)
    {
        this.config = config;
    }

    @Override
    public synchronized Database requestConnection(final String id)
            throws DatabaseNotRegisteredException
    {
        final DatabaseImpl database = connections.get(id);
        if (Objects.isNull(database))
            throw new DatabaseNotRegisteredException(id);
        else
            return database;
    }

    @Override
    public synchronized Database registerConnection(final String id,
            final String endpoint, final String user, final String password, final String table)
            throws SQLException
    {
        if (connections.containsKey(id))
            return connections.get(id);

        final String connection = String.format(System.getProperty(CUSTOM_DRIVER_FORMAT_PROPERTY, DEFAULT_DRIVER_FORMAT), table);
        final DatabaseImpl database = new DatabaseImpl(connection, user, password, id, table);
        connections.put(id, database);
        return database;
    }
}
