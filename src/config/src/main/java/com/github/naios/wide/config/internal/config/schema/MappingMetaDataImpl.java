
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.schema;

import com.github.naios.wide.api.config.schema.MappingMetaData;

public class MappingMetaDataImpl implements MappingMetaData
{
    private String name, target, description;

    private int index;

    private boolean key;

    private String alias;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getTarget()
    {
        return (target == null) ? name : target;
    }

    @Override
    public String getDescription()
    {
        return (description == null) ? "" : description;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public boolean isKey()
    {
        return key;
    }

    @Override
    public String getAlias()
    {
        return (alias == null) ? "" : alias;
    }

    @Override
    public String toString()
    {
        return String
                .format("MappingMetaData [name=%s, target=%s, description=%s, index=%s, key=%s, alias=%s]",
                        name, target, description, index, key, alias);
    }
}
