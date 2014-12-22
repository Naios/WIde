
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.google.common.reflect.TypeToken;

public class KnownSchemaDataTable<T extends ClientStorageStructure>
    extends AbstractDataTable<T>
{
    private final Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>> mapper;

    public KnownSchemaDataTable(final ClientStorage<T> storage, final TableSchema schema,
            final ByteBuffer buffer)
    {
        super(storage, buffer, schema.getFormat());

        mapper = createMapper(schema);

        // Build entry to Offset Cache
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<T> iterator()
    {
        return null;
    }
}
