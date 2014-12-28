
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.main;

import javafx.beans.property.StringProperty;

public interface ClientStorageConfig
{
    /**
     * @return The path to the schema
     */

    public StringProperty path();

    /**
     * @return The name of the schema
     */
    public StringProperty schema();
}
