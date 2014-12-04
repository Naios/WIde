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
