
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.Pair;
import com.google.common.reflect.TypeToken;

public abstract class AbstractDataTable<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>
{
    private final ClientStorage<T> storage;

    private final ClientStorageFormat format;

    private final Object[][] objects;

    public AbstractDataTable(final ClientStorage<T> storage, final ByteBuffer buffer, final ClientStorageFormat format)
    {
        this.storage = storage;

        Objects.requireNonNull(format);
        this.format = format;

        objects = new Object[storage.getRecordsCount()][getFormat().size()];

        for (int offset = storage.getDataBlockOffset(), y = 0;
                offset < storage.getStringBlockOffset();
                    offset += storage.getRecordSize(), ++y)
        {
            final ClientStorageRecord record = new ClientStorageRecord(buffer, storage, getFormat(), offset);

            int x = 0;
            for (final Pair<Integer, ClientStorageFormer> entry : getFormat())
            {
                final Object obj;
                switch (entry.second())
                {
                    case FT_BYTE:
                        obj = record.getByte(entry.first());
                        break;
                    case FT_INT:
                    case FT_IND:
                        obj = record.getInt(entry.first());
                        break;
                    case FT_LONG:
                        obj = record.getLong(entry.first());
                        break;
                    case FT_FLOAT:
                        obj = record.getFloat(entry.first());
                        break;
                    case FT_STRING:
                        obj = record.getString(entry.first());
                        break;
                    default:
                        obj = null;
                }

                objects[y][x++] = obj;
            }
        }
    }

    @Override
    public List<TypeToken<?>> getFieldType()
    {
        final List<TypeToken<?>> list = new ArrayList<>();
        getFormat().forEach(entry -> list.add(entry.second().getType()));
        return list;
    }

    @Override
    public Object[][] asObjectArray()
    {
        return objects;
    }

    public ClientStorage<T> getStorage()
    {
        return storage;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>>
        createMapper(final TableSchema schema)
    {
        return new JsonMapper(schema, ClientStorageRecordToPropertyMappingAdapterHolder.INSTANCE,
                            Arrays.asList(ClientStoragePrivateBase.class),
                                ClientStorageBaseImplementation.class);
    }

    @Override
    public ClientStorageFormat getFormat()
    {
        return format;
    }
}
