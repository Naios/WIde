
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import com.github.naios.wide.entities.util.EntityLoader;
import com.github.naios.wide.framework.internal.storage.name.NameStorageHolder;
import com.github.naios.wide.framework.internal.storage.name.NameStorageType;

public class AliasUtil
{
    @SuppressWarnings("rawtypes")
    public static Class<? extends Enum> getEnum(final String name)
    {
        return EntityLoader.requestEnum(name);
    }

    public static String getNamstorageEntry(final String name, final int entry)
    {
        final NameStorageType storage = NameStorageHolder.instance().get(name);
        if (storage == null)
            return null;

        final String value = storage.getStorage().request(entry);
        if (value == null)
            return null;
        else
            return storage.getPrefix() + value;
    }
}
