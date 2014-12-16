
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

class TableEntry
{
    private String name, target, description;

    private SchemaEntryType type;

    private int index;

    private boolean key;

    private String alias;

    public String getName()
    {
        return name;
    }

    public String getTarget()
    {
        return (target == null) ? name : target;
    }

    public String getDescription()
    {
        return description;
    }

    public SchemaEntryType getType()
    {
        return type;
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
        return alias;
    }
}
