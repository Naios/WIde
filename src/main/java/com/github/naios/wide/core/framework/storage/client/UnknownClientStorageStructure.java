
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import com.github.naios.wide.core.framework.storage.StorageName;

@StorageName(name=".*")
public class UnknownClientStorageStructure extends ClientStorageStructure
{
    @ClientStorageEntry(idx=0, name="Unknown Entry", key=true)
    private int entry;

    public int getEntry()
    {
        return entry;
    }
}
