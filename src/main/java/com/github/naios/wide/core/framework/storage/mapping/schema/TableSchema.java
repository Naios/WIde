
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping.schema;

import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.MappingMetaData;

public class TableSchema
{
    private String name, structure;

    private List<MappingMetaData> entries;

    public String getName()
    {
        return name;
    }

    public String getStructure()
    {
        return structure;
    }

    public List<MappingMetaData> getEntries()
    {
        return entries;
    }
}
