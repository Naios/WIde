package com.github.naios.wide.core.framework.storage.namestorage;

import java.util.HashMap;
import java.util.Map;

public class NameStorageHolder
{
    private final static NameStorageHolder INSTANCE =
            new NameStorageHolder();

    private final Map<String, NameStorage> holder =
            new HashMap<>();

    public NameStorageHolder()
    {
        for (final NameStorageType storage : NameStorageType.values())
            holder.put(storage.getId(), storage.getStorage());
    }

    public NameStorage get(final String id)
    {
        return holder.get(id);
    }

    public NameStorage get(final NameStorageType storage)
    {
        return storage.getStorage();
    }

    public static NameStorageHolder instance()
    {
        return INSTANCE;
    }
}
