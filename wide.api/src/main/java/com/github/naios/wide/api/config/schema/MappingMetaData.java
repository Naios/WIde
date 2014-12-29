
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

public interface MappingMetaData
{
    public String getName();

    public String getTarget();

    public String getDescription();

    public int getIndex();

    public boolean isKey();

    public String getAlias();
}
