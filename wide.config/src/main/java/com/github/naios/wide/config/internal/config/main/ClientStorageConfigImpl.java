
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.ClientStorageConfig;
import com.github.naios.wide.config.internal.util.Saveable;

public class ClientStorageConfigImpl implements ClientStorageConfig, Saveable
{
    private StringProperty path = new SimpleStringProperty(""),
            schema = new SimpleStringProperty("");

    @Override
    public StringProperty path()
    {
        return path;
    }

    @Override
    public StringProperty schema()
    {
        return schema;
    }

    @Override
    public void save()
    {
    }
}
