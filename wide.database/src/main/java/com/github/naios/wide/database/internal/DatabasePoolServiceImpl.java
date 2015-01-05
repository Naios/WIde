
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database.internal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.DatabaseConfig;
import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.database.DatabaseNotRegisteredException;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.database.UncheckedSQLException;
import com.github.naios.wide.api.util.FormatterWrapper;

public final class DatabasePoolServiceImpl
    implements DatabasePoolService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePoolServiceImpl.class);

    private ConfigService config;

    /**
     * The default jdbc driver to initialize
     */
    private final static String DEFAULT_DRIVER = "org.mariadb.jdbc.Driver";

    /**
     * Its possible to set this property to replace the default driver.
     */
    private final static String CUSTOM_DRIVER_PROPERTY = "com.github.naios.wide.database.custom_driver";

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
            LOGGER.error("Didn't find jdbc class {}", name);
        }

        for (final DatabaseConfig dbconfig : config.getActiveEnviroment().getDatabases())
            connections.put(dbconfig.id().get(), new SimpleObjectProperty<>(createDatabase(dbconfig)));

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde DatabasePool Service opened!");
    }

    public void close()
    {
        connections.forEach((id, database) ->
        {
            database.get().close();
            database.set(null);
        });
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde DatabasePool Service: closed!");
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
    {
        if (connections.containsKey(id))
            return (ObjectProperty)connections.get(id);

        return registerDatabase(id, endpoint, user, password, table, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SimpleObjectProperty<Database> registerDatabase(final String id,
            final String endpoint, final String user, final String password,
            final String table, final boolean optional)
    {
        final SimpleObjectProperty<DatabaseImpl> database = new SimpleObjectProperty<DatabaseImpl>(
                createDatabase(id, endpoint, user, password, table, optional));
        connections.put(id, database);

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Registered database {}", database);

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

    /**
     * OSGI Command
     * @return
     */
    @Descriptor("Returns all registered databases.")
    public synchronized List<String> databases()
    {
        final List<String> result = new ArrayList<>();
        connections.forEach((id, database) ->
        {
            result.add(String.format("%-10s - %s", id, database != null ? database.get() : null));
        });
        return result;
    }

    /**
     * OSGI Command
     */
    @Descriptor("Executes a query on the database with the given id.")
    public Collection<Collection<Object>> sql(@Descriptor("The id of the database (auth|character|world|<custom>)") final String id,
            @Descriptor("The SQL query you want to execute") final String query)
    {
        final Collection<Collection<Object>> list = new LinkedList<>();

        final Database db = requestConnection(id).get();
        db.open();

        final ResultSet result = db.execute(query);
        if (Objects.isNull(result))
        {
            System.out.println("No result!");
            return list;
        }

        ResultSetMetaData metaData;
        try
        {
            metaData = result.getMetaData();
        }
        catch (final SQLException e)
        {
            throw new UncheckedSQLException(e);
        }

        // TODO is this correct?
        if (Objects.isNull(metaData))
        {
            System.out.println("No metadata!");
            return list;
        }

        try
        {
            final int columns = metaData.getColumnCount();

            // Header
            final Collection<Object> header = new LinkedList<>();
            list.add(header);

            for (int i = 1; i <= columns; ++i)
                header.add(metaData.getColumnName(i));

            while (result.next())
            {
                final List<Object> values = new LinkedList<>();

                for (int i = 1; i <= columns; ++i)
                {
                    switch (metaData.getColumnType(i))
                    {
                        case Types.INTEGER:
                        case Types.BIGINT:
                        case Types.TINYINT:
                        case Types.SMALLINT:
                            values.add(result.getInt(i));
                            break;
                        case Types.DOUBLE:
                            values.add(result.getDouble(i));
                            break;
                        case Types.FLOAT:
                            values.add(result.getFloat(i));
                            break;
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.LONGVARCHAR:
                        case Types.NVARCHAR:
                            values.add(new FormatterWrapper(result.getString(i)));
                            break;
                        default:
                            values.add(result.getString(i));
                            break;
                    }
                }

                list.add(values);
            }

            result.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * OSGI Command
     */
    @Descriptor("Registers a database with the given id.")
    public void register(@Descriptor("id") final String id,
            @Descriptor("endpoint (localhost:3306)") final String endpoint,
                @Descriptor("user") final String user, @Descriptor("password") final String password,
                    @Descriptor("table") final String table)
    {
        registerConnection(id, endpoint, user, password, table);
    }
}
