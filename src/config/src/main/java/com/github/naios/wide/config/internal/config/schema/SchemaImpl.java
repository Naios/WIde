
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.WIdeConstants;
import com.github.naios.wide.api.config.schema.Schema;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.config.internal.ConfigHolder;

@SuppressWarnings("serial")
class MissingSchemaException extends RuntimeException
{
    public MissingSchemaException(final String name)
    {
        super(String.format("Schema %s is missing!!", name));
    }
}

public class SchemaImpl implements Schema
{
    private StringProperty name = new SimpleStringProperty(""),
                description = new SimpleStringProperty(""),
                    version = new SimpleStringProperty(
                            WIdeConstants.VERSION_WIDE_SCHEMATIC_CONFIG.toString());

    private Map<String, TableSchemaImpl> tables = new HashMap<>();

    @Override
    public ReadOnlyStringProperty name()
    {
        return name;
    }

    @Override
    public ReadOnlyStringProperty description()
    {
        return description;
    }

    @Override
    public StringProperty version()
    {
        return version;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Entry<String, TableSchema>> getTables()
    {
        return (Set)tables.entrySet();
    }

    @Override
    public TableSchema getSchemaOf(final String name)
    {
        final TableSchema schema = tables.get(name);
        if (Objects.nonNull(schema))
            return schema;
        else
            throw new MissingSchemaException(name);
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
