
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration;

import javafx.beans.property.StringProperty;

/**
 * The {@link DatabaseConfig} holds our data relevant for sql connections
 */
public interface DatabaseConfig
{
    /**
     * @return The database id ("world" or "auth" for example)
     */
    public StringProperty id();

    /**
     * @return The database name in the db system
     */
    public StringProperty name();

    /**
     * @return The hostname ("localhost:3306" as example)
     */
    public StringProperty host();

    /**
     * @return The username
     */
    public StringProperty user();

    /**
     * @return The password
     */
    public StringProperty password();

    /**
     * @return The schema name
     */
    public StringProperty schema();

    /**
     * @return The connection representing string (for example "user@localhost")
     */
    public StringProperty connection();
}
