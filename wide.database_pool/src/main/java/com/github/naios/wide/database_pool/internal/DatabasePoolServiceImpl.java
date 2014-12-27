package com.github.naios.wide.database_pool.internal;

import java.sql.SQLException;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableMap;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.DatabaseNotRegisteredException;
import com.github.naios.wide.database_pool.DatabasePoolService;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
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

    private final ObservableMap<String /*id*/, ObjectProperty<Database> /*database*/> connections =
            new SimpleMapProperty<>();

    @Override
    public ObjectProperty<Database> requestConnection(final String id)
            throws DatabaseNotRegisteredException
    {
        final ObjectProperty<Database> database = connections.get(id);
        if (Objects.isNull(database))
            throw new DatabaseNotRegisteredException(id);
        else
            return database;
    }

    @Override
    public ObjectProperty<Database> registerConnection(final String id,
            final String endpoint, final String user, final String password, final String table)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void start()
    {

    }

    public void stop()
    {

    }
}
