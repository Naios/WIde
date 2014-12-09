
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

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
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        final ServerStorageKey other = (ServerStorageKey) obj;
        if (!Arrays.equals(keys, other.keys))
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(keys);
        return result;
    }
}
