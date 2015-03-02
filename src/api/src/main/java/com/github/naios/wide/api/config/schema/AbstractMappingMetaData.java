
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

public abstract class AbstractMappingMetaData
    implements MappingMetaData
{
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isKey() ? 1231 : 1237);
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getTarget() == null) ? 0 : getTarget().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MappingMetaData))
            return false;
        final MappingMetaData other = (MappingMetaData) obj;
        if (isKey() != other.isKey())
            return false;
        if (getName() == null)
        {
            if (other.getName() != null)
                return false;
        }
        else if (!getName().equals(other.getName()))
            return false;
        if (getTarget() == null)
        {
            if (other.getTarget() != null)
                return false;
        }
        else if (!getTarget().equals(other.getTarget()))
            return false;
        return true;
    }
}
