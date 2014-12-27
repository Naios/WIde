
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.UncheckedSQLException;

public class DatabaseImpl implements Database
{
    private Connection connection;

    private final BooleanProperty isOpen =
            new SimpleBooleanProperty();

    private final String id, table;

    public DatabaseImpl(final String connection, final String user, final String password,
            final String id, final String table) throws SQLException
    {
        this.id = id;
        this.table = table;
    }

    protected synchronized void update()
    {
        if (updateConnectionStatus())
        {

        }
    }

    public String getId()
    {
        return id;
    }

    public String getTable()
    {
        return table;
    }

    @Override
    public synchronized ReadOnlyBooleanProperty isOpen()
    {
        return isOpen;
    }

    @Override
    public synchronized void directExecute(final String query, final Object... format)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

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
        if (updateConnectionStatus())
        {
            try
            {
                connection.close();
            }
            catch (final SQLException e)
            {
                e.printStackTrace();
            }

            isOpen.set(false);
        }
    }

    private boolean updateConnectionStatus()
    {
        if (!isOpen.get())
            return false;

        try
        {
            if (Objects.isNull(connection)
                || connection.isClosed())
            {
                isOpen.set(false);
                return false;
            }
        }
        catch (final SQLException e)
        {
            isOpen.set(false);
            return false;
        }

        return true;
    }
}
