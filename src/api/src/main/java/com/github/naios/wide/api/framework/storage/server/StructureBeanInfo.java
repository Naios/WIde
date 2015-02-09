
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import com.github.naios.wide.api.config.schema.MappingMetaData;

public interface StructureBeanInfo
{
    public ServerStorageStructure getStructure();

    public MappingMetaData getMappingMetaData();
}
