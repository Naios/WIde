
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class JsonMappingPlan implements MappingPlan
{
    private final BiMap<Integer, String> nameToOrdinal =
            HashBiMap.create();

    private final List<Integer> keys =
            new ArrayList<>();

    private final TableSchema schema;

    public JsonMappingPlan(final TableSchema schema)
    {
        this.schema = schema;

        // Cache values
        for (int i = 0; i < getMetadata().size(); ++i)
        {
            nameToOrdinal.put(i, getMetadata().get(i).getTarget());

            if (getMetadata().get(i).isKey())
                keys.add(i);
        }
    }

    @Override
    public int getNumberOfElements()
    {
        return schema.getEntries().size();
    }

    @Override
    public List<Integer> getKeys()
    {
        return keys;
    }

    @Override
    public List<MappingMetadata> getMetadata()
    {
        return schema.getEntries();
    }

    @Override
    public String getNameOfOrdinal(final int ordinal)
    {
        return nameToOrdinal.get(ordinal);
    }

    @Override
    public int getOrdinalOfName(final String name)
    {
        return nameToOrdinal.inverse().get(name);
    }
}
