
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.database;

public enum DatabaseType
{
    AUTH("auth", true),
    LOGON("logon", false),
    CHARACTER("character", true),
    WORLD("world", true),
    WPP("wpp", false);

    private final String id;

    private final boolean required;

    private DatabaseType(final String id, final boolean required)
    {
        this.id  = id;
        this.required = required;
    }

    public String getId()
    {
        return id;
    }

    public boolean isRequired()
    {
        return required;
    }

    public static DatabaseType getFromId(final String id)
    {
        for (final DatabaseType me : values())
            if (me.id.equals(id))
                return me;

        return null;
    }
}
