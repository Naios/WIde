
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping.schema;

import java.util.List;

import com.github.naios.wide.framework.internal.storage.client.ClientStorageFormatImpl;
import com.github.naios.wide.framework.internal.storage.mapping.MappingMetaData;

public class TableSchema
{
    private String name, structure;

    private ClientStorageFormatImpl format;

    private List<MappingMetaData> entries;

    public String getName()
    {
        return (name == null) ? "" : name;
    }

    public String getStructure()
    {
        return (structure == null) ? "" : structure;
    }

    public ClientStorageFormatImpl getFormat()
    {
        return format;
    }

    public List<MappingMetaData> getEntries()
    {
        return entries;
    }
}
