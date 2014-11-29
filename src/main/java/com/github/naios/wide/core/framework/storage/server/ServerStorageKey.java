package com.github.naios.wide.core.framework.storage.server;

import java.util.Arrays;

import javafx.beans.value.ObservableValue;

public class ServerStorageKey<T extends ServerStorageStructure>
{
    private final Object[] keys;

    public ServerStorageKey(final Object... keys)
    {
        this.keys = new Object[keys.length];
        for (int i = 0; i < keys.length; ++i)
            if (keys[i] instanceof ObservableValue<?>)
                this.keys[i] = ((ObservableValue<?>)keys[i]).getValue();
            else
                this.keys[i] = keys[i];
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
