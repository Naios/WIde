
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.name;

import java.util.HashMap;
import java.util.Map;

public class NameStorageHolder
{
    private final static NameStorageHolder INSTANCE =
            new NameStorageHolder();

    private final Map<String, NameStorageType> holder =
            new HashMap<>();

    public NameStorageHolder()
    {
        for (final NameStorageType storage : NameStorageType.values())
            holder.put(storage.getId(), storage);
    }

    public NameStorageType get(final String id)
    {
        return holder.get(id);
    }

    public static NameStorageHolder instance()
    {
        return INSTANCE;
    }
}
