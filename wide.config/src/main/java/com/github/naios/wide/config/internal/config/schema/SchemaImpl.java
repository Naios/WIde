
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.schema;

import java.util.ArrayList;
import java.util.List;

import com.github.naios.wide.api.config.schema.Schema;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.config.internal.util.ConfigHolder;

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
    private String name, description;

    private List<TableSchemaImpl> tables = new ArrayList<>();

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<TableSchema> getTables()
    {
        return (List)tables;
    }

    @Override
    public TableSchema getSchemaOf(final String name)
    {
        for (final TableSchema schema : tables)
            if (schema.getName().equals(name))
                return schema;

        throw new MissingSchemaException(name);
    }

    @Override
    public String toString()
    {
        return ConfigHolder.getJsonOfObject(this);
    }
}
