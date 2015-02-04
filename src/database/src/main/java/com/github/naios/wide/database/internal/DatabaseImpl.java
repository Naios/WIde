
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.database.UncheckedSQLException;

@SuppressWarnings("serial")
class DatabaseClosedException extends UncheckedSQLException
{
    public DatabaseClosedException()
    {
        super("Tried to access closed database!");
    }
}

public class DatabaseImpl implements Database
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseImpl.class);

    private Connection syncConnection, asyncConnection;

    private final BooleanProperty alive = new SimpleBooleanProperty(false);

    private final String id, name, connectionString, user, password;

    private final boolean optional;

    private final Map<Object, PreparedStatement> preparedStatements =
            new HashMap<>();

    private final Map<Object, String> preparedStatementQueries =
            new HashMap<>();

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public DatabaseImpl(final String connectionString, final String user, final String password,
            final String id, final String name, final boolean optional)
    {
        this.connectionString = connectionString;
        this.user = user;
        this.password =  password;
        this.id = id;
        this.name = name;
        this.optional = optional;

        // Try to open the database
        this.alive.set(open());
    }

    @Override
    public boolean open()
    {
        if (alive.get())
            return true;

        try
        {
            this.syncConnection = DriverManager.getConnection(connectionString, user, password);
            this.asyncConnection = DriverManager.getConnection(connectionString, user, password);

            for (final Entry<Object, String> query : preparedStatementQueries.entrySet())
                preparedStatements.put(query.getKey(), syncConnection.prepareStatement(query.getValue()));
        }
        catch (final SQLException e)
        {
            e.printStackTrace();

            if (Objects.nonNull(this.syncConnection))
                try
                {
                    this.syncConnection.close();
                }
                catch (final Exception e2)
                {
                }

            this.syncConnection = null;

            if (Objects.nonNull(this.asyncConnection))
                try
                {
                    this.asyncConnection.close();
                }
                catch (final Exception e2)
                {
                }

            this.asyncConnection = null;

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Connection to {} failed!", connectionString);
            return false;
        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Established sync & async connection to {}.", connectionString);
        return true;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    protected boolean isOptional()
    {
        return optional;
    }

    @Override
    public ReadOnlyBooleanProperty alive()
    {
        return alive;
    }

    private static String lazyFormat(final String query, final Object[] args)
    {
        if (args.length == 0)
            return query;
        else
            return String.format(query, args);
    }

    private ResultSet execute(final Connection connection, final String query, final Object[] args) throws UncheckedSQLException
    {
        updateAliveStatus();
        try
        {
            final Statement statement = syncConnection.createStatement();
            final ResultSet result  = statement.executeQuery(lazyFormat(query, args));

            statement.closeOnCompletion();
            return result;
        }
        catch (final SQLException e)
        {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void simpleAsyncExecute(final String query, final Object... args)
            throws UncheckedSQLException
    {
        updateAliveStatus();
        pool.submit(() ->
        {
            try
            {
                final Statement statement = asyncConnection.createStatement();
                statement.execute(lazyFormat(query, args));
                statement.close();
            }
            catch (final SQLException e)
            {
                throw new UncheckedSQLException(e);
            }
        });
    }

    @Override
    public ResultSet execute(final String query, final Object... format)
            throws UncheckedSQLException
    {
        updateAliveStatus();
        return execute(syncConnection, query, format);
    }

    @Override
    public Future<ResultSet> asyncExecute(final String query, final Object... format)
    {
        updateAliveStatus();
        return pool.submit(() -> execute(asyncConnection, query, format));
    }

    @Override
    public void createPreparedStatement(final Object id, final String statement)
            throws UncheckedSQLException
    {
        if (preparedStatementQueries.containsKey(id))
            throw new UncheckedSQLException(String.format("Prepared statement %s already exists!", id));

        updateAliveStatus();

        preparedStatementQueries.put(id, statement);

        try
        {
            preparedStatements.put(id, syncConnection.prepareStatement(statement));
        }
        catch (final SQLException e)
        {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void releasePreparedStatement(final Object id)
            throws UncheckedSQLException
    {
        updateAliveStatus();
        preparedStatementQueries.remove(id);

        final PreparedStatement statement = preparedStatements.get(id);
        synchronized (statement)
        {
            if (Objects.nonNull(statement))
            {
                preparedStatements.remove(id);

                try
                {
                    statement.closeOnCompletion();
                }
                catch (final SQLException e)
                {
                    throw new UncheckedSQLException(e);
                }
            }
        }
    }

    @Override
    public ResultSet preparedExecute(final Object id, final Object... args)
            throws UncheckedSQLException
    {
        updateAliveStatus();
        final PreparedStatement statement = preparedStatements.get(id);
        if (Objects.nonNull(statement))
            synchronized (statement)
            {
               try
                {
                    for (int i = 0; i < args.length; ++i)
                        if (args[i] instanceof Integer)
                            statement.setInt(i + 1, (int)args[i]);
                        else if (args[i] instanceof Boolean)
                            statement.setBoolean(i + 1, (boolean)args[i]);
                        else if (args[i] instanceof Float)
                            statement.setFloat(i + 1, (float)args[i]);
                        else if (args[i] instanceof Double)
                            statement.setDouble(i + 1, (double)args[i]);
                        else
                            statement.setString(i + 1, args[i].toString());

                    return statement.executeQuery();
                }
                catch (final SQLException e)
                {
                    throw new UncheckedSQLException(e);
                }
            }

        throw new UncheckedSQLException(String.format("Prepared statement %s doesn't exists!", id));
    }

    protected void close()
    {
        // Forces shut down and executes all pending tasks in the current thread
        pool.shutdownNow().forEach(task -> task.run());
        if (updateAliveStatus())
        {
            try
            {
                for (final PreparedStatement statement : preparedStatements.values())
                    statement.close();

                preparedStatements.clear();

                syncConnection.close();
                asyncConnection.close();
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
            }

            alive.set(false);
        }
    }

    private boolean updateAliveStatus()
    {
        if (!alive.get())
            return false;

        try
        {
            if (Objects.isNull(syncConnection)
                || syncConnection.isClosed()
                || Objects.isNull(asyncConnection)
                || asyncConnection.isClosed())
            {
                alive.set(false);
                throw new DatabaseClosedException();
            }
        }
        catch (final SQLException e)
        {
            alive.set(false);
            throw new DatabaseClosedException();
        }

        return true;
    }

    @Override
    public String toString()
    {
        return String.format("%s@%s", user, connectionString);
    }
}
