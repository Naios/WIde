
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.database;

import java.sql.SQLException;

import javafx.beans.property.ObjectProperty;

/**
 * The Pool service is responsible for creating and managing {@link Database} instances
 */
public interface DatabasePoolService
{
    /**
     * @param id    The unique id of the database ("world" or "auth" for example)<br>
     *              in most cases this id is present and configured in the config file,
     *              however you are allowed to register temp id's too.
     *
     * @return      Returns the {@link Database} object.
     *
     * @throws      DatabaseNotRegisteredException Throws {@link DatabaseNotRegisteredException} if the requested database wasn't registered
     * @throws      SQLException Throws {@link SQLException} that occur while trying to open a connection.
     */
    public ObjectProperty<Database> requestConnection(String id) throws DatabaseNotRegisteredException, SQLException;

    /**
     * Create a temporarily entry to a
     *
     * @param id        The unique id of the database ("world" or "auth" for example)<br>
     *                  in most cases this id is present and configured in the config file,
     *                  however you are allowed to register temp id's too.
     *
     * @param endpoint  Represents the endpoint you want to connecto to, "localhost:3306" for example.
     * @param user      Represents the sql user
     * @param password  Represents the sql password
     * @param table     Represents the sql table name
     *
     * @return          Returns a {@link Database} object that on success.
     *
     * @throws          SQLException Throws {@link SQLException} that occur while trying to open a connection.
     */
    public ObjectProperty<Database> registerConnection(String id, String endpoint,
            String user, String password, String table) throws SQLException;
}
