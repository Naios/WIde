
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;

public abstract class AbstractDataTable<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>
{
    private final ClientStorage<T> storage;

    private final ByteBuffer buffer;

    private final Map<Integer, Integer> entryToOffsetCache =
            new HashMap<>();

    public AbstractDataTable(final ClientStorage<T> storage, final ByteBuffer buffer)
    {
        this.storage = storage;

        this.buffer = buffer;
    }

    public ClientStorage<T> getStorage()
    {
        return storage;
    }

    public ByteBuffer getBuffer()
    {
        return buffer;
    }

    protected Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>>
        createMapper(final TableSchema schema)
    {
        return new JsonMapper<>(schema, Arrays.asList(ClientStoragePrivateBase.class),
                ClientStorageBaseImplementation.class);
    }

    protected void addEntryToOffset(final int entry, final int offset)
    {
        entryToOffsetCache.put(entry, offset);
    }

    protected int getOffsetOfEntry(final int entry)
    {
        return entryToOffsetCache.get(entry);
    }

    protected int getNumberOfEntries()
    {
        return entryToOffsetCache.size();
    }
}
