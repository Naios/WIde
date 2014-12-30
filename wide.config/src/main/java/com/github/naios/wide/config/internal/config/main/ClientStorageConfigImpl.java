
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.ClientStorageConfig;
import com.github.naios.wide.api.config.schema.Schema;
import com.github.naios.wide.config.internal.config.schema.SchemaImpl;
import com.github.naios.wide.config.internal.util.ConfigHolder;

public class ClientStorageConfigImpl implements ClientStorageConfig
{
    private StringProperty path = new SimpleStringProperty(""),
            schema = new SimpleStringProperty("");

    private final static String DEFAULT_SCHEMA_PATH = "default/schematics/Client.json";

    private final ConfigHolder<SchemaImpl> schemaObject =
            new ConfigHolder<>(DEFAULT_SCHEMA_PATH, SchemaImpl.class);

    @Override
    public StringProperty path()
    {
        return path;
    }

    @Override
    public StringProperty schemaPath()
    {
        return schema;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ObjectProperty<Schema> schema()
    {
        return (ObjectProperty)schemaObject.get(schemaPath().get());
    }

    @Override
    public String toString()
    {
        return ConfigHolder.getJsonOfObject(this);
    }
}
