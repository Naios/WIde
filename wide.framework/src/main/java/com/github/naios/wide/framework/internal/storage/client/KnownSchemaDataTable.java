
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.framework.internal.storage.mapping.Mapper;
import com.github.naios.wide.framework.internal.storage.mapping.MappingMetaData;
import com.github.naios.wide.framework.internal.storage.mapping.schema.TableSchema;
import com.github.naios.wide.framework.storage.client.ClientStorageException;
import com.github.naios.wide.framework.storage.client.ClientStorageStructure;

public class KnownSchemaDataTable<T extends ClientStorageStructure>
    extends AbstractDataTable<T>
{
    private final Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>> mapper;

    private final Map<Integer, T> entries =
            new HashMap<>();

    private final List<String> names, description;

    @SuppressWarnings("unchecked")
    public KnownSchemaDataTable(final ClientStorageImpl<T> storage, final TableSchema schema,
            final ByteBuffer buffer)
    {
        super(storage, buffer, schema.getFormat());

        mapper = createMapper(schema);

        for (int i = storage.getDataBlockOffset();
                i < storage.getStringBlockOffset();
                    i += storage.getRecordSize())
        {
            final ClientStorageRecord record = new ClientStorageRecord(buffer, storage, getFormat(), i);

            final T entry = (T) mapper.map(record);
            entries.put((int) entry.getRawKeys().get(0), entry);
        }

        final Map<Integer, MappingMetaData> metaDataOfIndex = new HashMap<>();
        mapper.getPlan().getMetadata().forEach(data -> metaDataOfIndex.put(data.getIndex(), data));

        names = new ArrayList<>(getFormat().size());
        description = new ArrayList<>(getFormat().size());

        getFormat().forEach(entry ->
        {
            if (metaDataOfIndex.containsKey(entry.first()))
            {
                names.add(metaDataOfIndex.get(entry.first()).getName());
                description.add(metaDataOfIndex.get(entry.first()).getDescription());
            }
            else
            {
                names.add("Column " + entry.first());
                description.add("");
            }
        });
    }

    @Override
    public List<String> getFieldNames()
    {
        return names;
    }

    @Override
    public List<String> getFieldDescription()
    {
        return description;
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        return entries.get(entry);
    }

    @Override
    public Iterator<T> iterator()
    {
        return entries.values().iterator();
    }
}
