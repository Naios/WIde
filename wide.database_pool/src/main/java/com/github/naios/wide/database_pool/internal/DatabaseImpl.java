
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.UncheckedSQLException;

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
    private Connection syncConnection, asyncConnection;

    private final BooleanProperty alive;

    private final String id, table, connectionString, user, password;

    private final boolean optional;

    private final Map<Object, PreparedStatement> preparedStatements =
            new HashMap<>();

    private final Map<Object, String> preparedStatementQuerys =
            new HashMap<>();

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public DatabaseImpl(final String connectionString, final String user, final String password,
            final String id, final String table, final boolean optional)
    {
        this.connectionString = connectionString;
        this.user = user;
        this.password =  password;
        this.id = id;
        this.table = table;
        this.optional = optional;

        // Try to open the database
        alive = new SimpleBooleanProperty(open());
    }

    @Override
    public boolean open()
    {
        try
        {
            System.out.println(String.format("DB: %s, User: %s, PW: %s", connectionString, user, password));

            this.syncConnection = DriverManager.getConnection(connectionString, user, password);
            this.asyncConnection = DriverManager.getConnection(connectionString, user, password);

            /*
            for (final Entry<Object, String> query : preparedStatementQuerys.entrySet())
                preparedStatements.put(query.getKey(), syncConnection.prepareStatement(query.getValue()));
                */
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            this.syncConnection = null;
            this.asyncConnection = null;
            return false;
        }

        return false;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return table;
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
        if (preparedStatementQuerys.containsKey(id))
            throw new UncheckedSQLException(String.format("Prepared statement %s already exists!", id));

        updateAliveStatus();

        preparedStatementQuerys.put(id, statement);

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
        preparedStatementQuerys.remove(id);

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
                            statement.setInt(i, (int)args[i]);
                        else if (args[i] instanceof Boolean)
                            statement.setBoolean(i, (boolean)args[i]);
                        else if (args[i] instanceof Float)
                            statement.setFloat(i, (float)args[i]);
                        else if (args[i] instanceof Double)
                            statement.setDouble(i, (double)args[i]);
                        else
                            statement.setString(i, args[i].toString());

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
        pool.shutdown();
        try
        {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (final InterruptedException e1)
        {
        }

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
            throw new DatabaseClosedException();

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
}
