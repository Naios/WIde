
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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (key ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MappingMetaDataImpl))
            return false;
        final MappingMetaDataImpl other = (MappingMetaDataImpl) obj;
        if (key != other.key)
            return false;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (target == null)
        {
            if (other.target != null)
                return false;
        }
        else if (!target.equals(other.target))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String
                .format("MappingMetaData [name=%s, target=%s, description=%s, index=%s, key=%s, alias=%s]",
                        name, target, description, index, key, alias);
    }
}
