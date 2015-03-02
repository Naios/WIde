
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.schema;

import java.util.List;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.SchemaPolicy;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormatImpl;
import com.github.naios.wide.config.internal.ConfigHolder;

public class TableSchemaImpl implements TableSchema
{
    private String name;

    private SchemaPolicy policy = SchemaPolicy.LAZY;

    private String structure;

    private ClientStorageFormatImpl format;

    private List<MappingMetaDataImpl> entries;

    @Override
    public String getName()
    {
        return (name == null) ? "" : name;
    }

    @Override
    public SchemaPolicy getPolicy()
    {
        return policy;
    }

    @Override
    public String getStructure()
    {
        return (structure == null) ? "" : structure;
    }

    @Override
    public ClientStorageFormat getFormat()
    {
        return format;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<MappingMetaData> getEntries()
    {
        return (List)entries;
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
