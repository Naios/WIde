
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.List;

class MissingSchemaException extends RuntimeException
{
    public MissingSchemaException(final String name)
    {
        super(String.format("Schema %s is missing!!", name));
    }
}

public class Schema
{
    private String name, description;

    private List<TableSchema> tables;

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public List<TableSchema> getTables()
    {
        return tables;
    }

    public TableSchema getSchemaOf(final String name)
    {
        for (final TableSchema schema : tables)
            if (schema.getName().equals(name))
                return schema;

        throw new MissingSchemaException(name);
    }
}
