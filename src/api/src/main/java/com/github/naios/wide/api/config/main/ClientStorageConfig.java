
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

public interface ClientStorageConfig
{
    /**
     * @return The path to the client storage directory
     */
    public StringProperty path();

    /**
     * @return The path of the schema
     */
    public StringProperty schemaPath();

    /**
     * @return Returns the schema matching the schema Path
     */
    public ReadOnlyObjectProperty<Schema> schema();
}
