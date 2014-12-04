package com.github.naios.wide.core.framework.storage.name;

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
