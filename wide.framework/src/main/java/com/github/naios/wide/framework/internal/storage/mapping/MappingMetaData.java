
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

public class MappingMetaData
{
    private String name, target, description;

    private int index;

    private boolean key;

    private String alias;

    public String getName()
    {
        return name;
    }

    protected String getTarget()
    {
        return (target == null) ? name : target;
    }

    public String getDescription()
    {
        return (description == null) ? "" : description;
    }

    public int getIndex()
    {
        return index;
    }

    public boolean isKey()
    {
        return key;
    }

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
