
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.helper;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public class ObservableValueStorageInfo
{
    private final ServerStorageStructure structure;

    private final String name;

    public ObservableValueStorageInfo(final ServerStorageStructure structure, final String name)
    {
        this.structure = structure;
        this.name = name;
    }

    public String getTableName()
    {
        return structure.getOwner().getTableName();
    }

    public ServerStorageStructure getStructure()
    {
        return structure;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ObservableValueStorageInfo other = (ObservableValueStorageInfo) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((structure == null) ? 0 : structure.hashCode());
        return result;
    }
}