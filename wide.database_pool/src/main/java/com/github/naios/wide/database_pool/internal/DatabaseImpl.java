
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool.internal;

import java.sql.Connection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.database_pool.Database;
import com.github.naios.wide.database_pool.UncheckedSQLException;

public class DatabaseImpl implements Database
{
    private Connection connection;

    private final BooleanProperty isOpen =
            new SimpleBooleanProperty();

    @Override
    public BooleanProperty isOpen()
    {
        return isOpen;
    }

    @Override
    public void directExecute(final String query, final Object... format)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void asyncExecute(final String query, final Object... format)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void createPreparedStatement(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void releasePreparedStatement(final Object id)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void directPreparedExecute(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void asyncPreparedExecute(final Object id, final String statement)
            throws UncheckedSQLException
    {
        // TODO Auto-generated method stub

    }
}
