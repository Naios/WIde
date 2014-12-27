
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.UncheckedSQLException;

public class DatabaseImpl implements Database
{
    private final Connection connection;

    private final BooleanProperty alive;

    private final String id, table;

    private final boolean optional;

    public DatabaseImpl(final String connection, final String user, final String password,
            final String id, final String table, final boolean optional) throws SQLException
    {
        this.connection = DriverManager.getConnection(connection, user, password);

        this.id = id;
        this.table = table;
        this.optional = optional;

        alive = new SimpleBooleanProperty(true);
    }

    public String getId()
    {
        return id;
    }

    public String getTable()
    {
        return table;
    }

    public boolean isOptional()
    {
        return optional;
    }

    @Override
    public ReadOnlyBooleanProperty alive()
    {
        return alive;
    }

    @Override
    public synchronized void directExecute(final String query, final Object... format)
            throws UncheckedSQLException
    {


    }

    @Override
    public synchronized void asyncExecute(final String query, final Object... format)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void createPreparedStatement(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void releasePreparedStatement(final Object id)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void directPreparedExecute(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void asyncPreparedExecute(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    protected synchronized void close()
    {
        if (updateAliveStatus())
        {
            try
            {
                connection.close();
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
            throw new UncheckedSQLException("");

        try
        {
            if (Objects.isNull(connection)
                || connection.isClosed())
            {
                alive.set(false);
                return false;
            }
        }
        catch (final SQLException e)
        {
            alive.set(false);
            return false;
        }

        return true;
    }
}
