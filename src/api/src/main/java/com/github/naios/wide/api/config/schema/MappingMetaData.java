
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

public interface MappingMetaData
{
    public String getName();

    public default String getTarget()
    {
        return "";
    }

    public default String getDescription()
    {
        return "";
    }

    public default String getDefaultValue()
    {
        return "";
    }

    public default int getIndex()
    {
        return 0;
    }

    public default boolean isKey()
    {
        return false;
    }

    public default String getAlias()
    {
        return "";
    }
}
