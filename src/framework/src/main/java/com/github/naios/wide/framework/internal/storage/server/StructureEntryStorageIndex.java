/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.Pair;

class StructureEntryStorageIndex
{
    private final ServerStorageStructure structure;

    private final Pair<ReadOnlyProperty<?>, MappingMetaData> entry;

    public StructureEntryStorageIndex(final ServerStorageStructure structure, final Pair<ReadOnlyProperty<?>, MappingMetaData> entry)
    {
        this.structure = structure;
        this.entry = entry;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (((entry != null) && (entry.second() != null)) ? 0 : entry.second().hashCode());
        result = prime * result + ((structure == null) ? 0 : structure.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof StructureEntryStorageIndex))
            return false;
        final StructureEntryStorageIndex other = (StructureEntryStorageIndex) obj;
        if (entry == null)
        {
            if (other.entry != null)
                return false;
        }
        else if (entry.second() == null)
        {
            if (other.entry.second() != null)
                return false;
        }
        else if (!entry.second().equals(other.entry.second()))
            return false;
        if (structure == null)
        {
            if (other.structure != null)
                return false;
        }
        else if (!structure.equals(other.structure))
            return false;
        return true;
    }
}