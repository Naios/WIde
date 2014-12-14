
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientStorageConfig
{
    private final StringProperty path;
    private final StringProperty schema;

    public ClientStorageConfig()
    {
        this.path = new SimpleStringProperty();
        this.schema = new SimpleStringProperty();
    }

    public StringProperty path()
    {
        return path();
    }

    public StringProperty schema()
    {
        return path();
    }
}
