package com.github.naios.wide.scripts.printdbc;

import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.storage.client.ClientStorageEntry;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;

@StorageName(name=".*")
public class UnknownStructure extends ClientStorageStructure
{
    @ClientStorageEntry(idx=0, name="Unknown Entry", key=true)
    private int entry;

    public int getEntry()
    {
        return entry;
    }
}
