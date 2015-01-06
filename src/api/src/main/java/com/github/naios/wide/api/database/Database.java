
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.database;

import java.sql.ResultSet;
import java.util.concurrent.Future;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * {@link Database} works as a wrapper interface for {@link java.sql.Connection}
 * to provide easier access and better support on environment switch/reconnection
 */
public interface Database
{
    /**
     * Tries to open the database
     * @return returns true on success
     */
    public boolean open();

    /**
     * @return Returns the database unique id
     */
    public String getId();

    /**
     * @return Returns the database name.
     */
    public String getName();

    /**
     * @return A boolean property that represents if the database is open
     */
    public ReadOnlyBooleanProperty alive();

    /**
     * A blocking query execute on the database that returns a result.
     *
     * @param query     The query you want to execute
     * @param format    Its possible to format the query with arguments
     *
     * @return          Returns the resulting result set.
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public ResultSet execute(String query, Object... args) throws UncheckedSQLException;

    /**
     * A non-blocking async execute on the database that returns a result.
     *
     * @param query     The query you want to execute
     * @param args      Its possible to format the query with arguments
     *
     * @return          Returns a future pointing to the future result set.
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public Future<ResultSet> asyncExecute(String query, Object... args) throws UncheckedSQLException;

    /**
     * A non-blocking async execute on the database that returns no result.
     *
     * @param query     The query you want to execute
     * @param args    Its possible to format the query with arguments
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public void simpleAsyncExecute(String query, Object... args) throws UncheckedSQLException;

    /**
     * Creates a prepared statement
     *
     * @param id        The unique id of the statement, ids must implement hashCode() and equals()!
     * @param statement The sql prepared statement itself
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public void createPreparedStatement(Object id, String statement) throws UncheckedSQLException;

    /**
     * Releaes (deletes) a prepared statement
     *
     * @param id The unique id of the statement, ids must implement hashCode() and equals()!
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public void releasePreparedStatement(Object id) throws UncheckedSQLException;

    /**
     * Executes a prepared statement on the database
     *
     * @param id    The unique id of the statement, ids must implement hashCode() and equals()!
     * @param args  All prepared arguments in the correct order.<br>
     *              If the object isn't a primitive type {@link Object#toString()} will be called.
     *
     * @return      Returns a {@link ResultSet} on success
     *
     * @throws UncheckedSQLException that wraps SQLException's that occurred
     */
    public ResultSet preparedExecute(Object id, Object... args) throws UncheckedSQLException;
}
