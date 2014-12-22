
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.google.common.reflect.TypeToken;

public class KnownSchemaDataTable<T extends ClientStorageStructure>
    extends AbstractDataTable<T>
{
    private final Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>> mapper;

    private final Map<Integer, T> entries =
            new HashMap<>();

    @SuppressWarnings("unchecked")
    public KnownSchemaDataTable(final ClientStorage<T> storage, final TableSchema schema,
            final ByteBuffer buffer)
    {
        super(storage, schema.getFormat());

        mapper = createMapper(schema);

        for (int i = storage.getDataBlockOffset();
                i < storage.getStringBlockOffset();
                    i += storage.getRecordSize())
        {
            final ClientStorageRecord record = new ClientStorageRecord(buffer, storage, getFormat(), i);

            final T entry = (T) mapper.map(record);
            entries.put((int) entry.getRawKeys().get(0), entry);
        }
    }

    @Override
    public List<String> getFieldNames()
    {
        final List<String> names = new ArrayList<>(mapper.getPlan().getNumberOfElements());
        mapper.getPlan().getMetadata().forEach(entry -> names.add(entry.getName()));
        return names;
    }

    @Override
    public List<String> getFieldDescription()
    {
        final List<String> description = new ArrayList<>(mapper.getPlan().getNumberOfElements());
        mapper.getPlan().getMetadata().forEach(entry -> description.add(entry.getDescription()));
        return description;
    }

    @Override
    public List<TypeToken<?>> getFieldType()
    {
        return mapper.getPlan().getMappedTypes();
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        return entries.get(entry);
    }

    @Override
    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        final Object[][] array = new Object[entries.size()][mapper.getPlan().getNumberOfElements()];

        // Order keys
        final Set<Integer> keys = new TreeSet<Integer>(entries.keySet());

        int y = 0;
        for (final int key : keys)
        {
            final T entry = getEntry(key);

            for (int x = 0; x < entry.getRawValues().size(); ++x)
                array[y][x] = FormatterWrapper.format(entry.getRawValues().get(x), prettyWrap);

            ++y;
        }

        return array;
    }

    @Override
    public Iterator<T> iterator()
    {
        return entries.values().iterator();
    }
}
