
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

import javafx.beans.value.ObservableValue;

public class ServerStorageKeyImpl<T extends ServerStorageStructure>
    implements ServerStorageKey<T>
{
    private final List<Object> keys;

    public ServerStorageKeyImpl(final Object... keyArray)
    {
        final List<Object> keys = new ArrayList<>(keyArray.length);

        for (int i = 0; i < keyArray.length; ++i)
            if (keyArray[i] instanceof ObservableValue<?>)
                keys.add(((ObservableValue<?>)keyArray[i]).getValue());
            else
                keys.add(keyArray[i]);

        this.keys = Collections.unmodifiableList(keys);
    }

    @Override
    public List<Object> get()
    {
        return keys;
    }

    @Override
    public Object get(final int index)
    {
        return keys.get(index);
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
        final ServerStorageKeyImpl other = (ServerStorageKeyImpl) obj;

        return keys.equals(other.get());
    }

    @Override
    public int hashCode()
    {
        return keys.hashCode();
    }
}
