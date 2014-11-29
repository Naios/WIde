package com.github.naios.wide.core.framework.storage.server;

import java.util.Arrays;

public class ServerStorageKey<T extends ServerStorageStructure>
{
    private final Object[] keys;

    public ServerStorageKey(final Object... keys)
    {
        this.keys = keys;
    }

    public Object[] get()
    {
        return keys;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(keys);
    }
}
