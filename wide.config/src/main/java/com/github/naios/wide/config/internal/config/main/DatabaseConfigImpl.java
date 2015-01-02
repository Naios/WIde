
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.DatabaseConfig;
import com.github.naios.wide.api.config.schema.Schema;
import com.github.naios.wide.api.util.LateAllocator;
import com.github.naios.wide.config.internal.config.schema.SchemaImpl;
import com.github.naios.wide.config.internal.util.ConfigHolder;

public class DatabaseConfigImpl implements DatabaseConfig
{
    private StringProperty id = new SimpleStringProperty(""),
                name = new SimpleStringProperty(""),
                    host = new SimpleStringProperty(""),
                        user = new SimpleStringProperty(""),
                            password = new SimpleStringProperty(""),
                                schema = new SimpleStringProperty("");

    // We need to late bind the connection property to user and host
    // because user & host might be null sometimes
    private final LateAllocator<StringProperty> connection = new LateAllocator<StringProperty>()
    {
        @Override
        public StringProperty allocate()
        {
            final StringProperty property = new SimpleStringProperty();
            property.bind(Bindings.concat(user, "@", host));
            return property;
        }
    };

    private final static String DEFAULT_SCHEMA_PATH = "default/schematics/Server.json";

    private final ConfigHolder<SchemaImpl> schemaObject =
            new ConfigHolder<>(DEFAULT_SCHEMA_PATH, SchemaImpl.class);

    @Override
    public StringProperty id()
    {
        return id;
    }

    @Override
    public StringProperty name()
    {
        return name;
    }

    @Override
    public StringProperty host()
    {
        return host;
    }

    @Override
    public StringProperty user()
    {
        return user;
    }

    @Override
    public StringProperty password()
    {
        return password;
    }

    @Override
    public StringProperty schemaPath()
    {
        return schema;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ObjectProperty<Schema> schema()
    {
        return (ObjectProperty)schemaObject.get(schemaPath().get());
    }

    @Override
    public StringProperty endpoint()
    {
        return connection.get();
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
