
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool;

import javafx.beans.property.BooleanProperty;

/**
 * {@link Database} works as a wrapper interface for {@link java.sql.Connection}
 * to provide easier access and better support on environment switch/reconnection
 */
public interface Database
{
    /**
     * @return A boolean property to represent if the database is open/ closed at the moment
     */
    public BooleanProperty isOpen();

    /**
     *
     * @param query
     * @param format
     * @throws UncheckedSQLException that wraps SQLException's that occured
     */
    public void directExecute(String query, Object... format) throws UncheckedSQLException;

    public void asyncExecute(String query, Object... format);

    public void createPreparedStatement(Object id, String statement) throws UncheckedSQLException;

    public void releasePreparedStatement(Object id) throws UncheckedSQLException;

    public void directPreparedExecute(Object id, String statement) throws UncheckedSQLException;

    public void asyncPreparedExecute(Object id, String statement) throws UncheckedSQLException;
}
