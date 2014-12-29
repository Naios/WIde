
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

import java.util.List;

import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;

public interface TableSchema
{
    public String getName();

    public String getStructure();

    public ClientStorageFormat getFormat();

    public List<MappingMetaData> getEntries();
}
