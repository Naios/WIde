
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.schema;

import com.github.naios.wide.api.config.schema.AbstractMappingMetaData;

public class MappingMetaDataImpl extends AbstractMappingMetaData
{
    private String name = "", target = "", description = "", category = "";

    private int index;

    private String defaultValue = "";

    private boolean key;

    private String alias = "";

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getTarget()
    {
        return target.isEmpty() ? name : target;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getCategory()
    {
        return category;
    }

    @Override
    public String getDefaultValue()
    {
        return defaultValue;
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
        return alias;
    }
}
