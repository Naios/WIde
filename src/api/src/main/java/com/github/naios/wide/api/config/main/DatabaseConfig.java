
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.schema.Schema;

/**
 * The {@link DatabaseConfig} holds our data relevant for sql connections
 */
public interface DatabaseConfig
{
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
     * @return The path of the schema
     */
    public StringProperty schemaPath();

    /**
     * @return Returns the schema matching the schema Path
     */
    public ReadOnlyObjectProperty<Schema> schema();

    /**
     * @return The endpoint representing string (for example "user@localhost")
     */
    public StringProperty endpoint();
}
