
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
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.github.naios.wide.core.framework.util.Pair;
import com.google.common.reflect.TypeToken;

class CheckInfo
{
    private final ClientStorage<?> storage;

    private final ByteBuffer buffer;

    private final StringBuilder format;

    private final int offset;

    public CheckInfo(final ClientStorage<?> storage, final ByteBuffer buffer,
            final StringBuilder format, final int offset)
    {
        this.storage = storage;
        this.format = format;
        this.buffer = buffer;
        this.offset = offset;
    }

    public ClientStorage<?> getStorage()
    {
        return storage;
    }

    public StringBuilder getFormat()
    {
        return format;
    }

    public ByteBuffer getBuffer()
    {
        return buffer;
    }

    public int getFieldOffset()
    {
        return offset;
    }
}

class FieldEstimater
{
    private final static int FLOAT_CHECK_BOUNDS = 100000000;

    private final static float CHECK_PERCENTAGE = 0.92f;

    private final static int CHECK_MAX_ENTRIES = 500;

    private final static float CHECK_COVERAGE = 0.3f;

    private final ClientStorageFormer defaultFormer;

    private final List<Pair<ClientStorageFormer, Predicate<CheckInfo>>> checks =
            new LinkedList<>();

    public FieldEstimater(final ClientStorageFormer defaultFormer)
    {
        this.defaultFormer = defaultFormer;

        this
            .register(ClientStorageFormer.FT_STRING, info ->
            {
                final int count = Math.min(info.getStorage().getRecordsCount(), CHECK_MAX_ENTRIES);

                int checked = 0;
                for (int y = 0; y < count; ++y)
                {
                    final int offset = info.getFieldOffset() + (info.getStorage().getRecordSize() * y) +
                            info.getStorage().getDataBlockOffset();

                    final int value = info.getBuffer().getInt(offset);

                    if (value == 0)
                        continue;

                    if (info.getStorage().getStringTable().getString(value) == null)
                        return false;

                    ++checked;
                }

                return checked > (count * CHECK_COVERAGE);
            })
            .register(ClientStorageFormer.FT_FLOAT, info ->
            {
                final int count = Math.min(info.getStorage().getRecordsCount(), CHECK_MAX_ENTRIES);

                int match = 0, checked = 0;
                for (int y = 0; y < count; ++y)
                {
                    final int offset = info.getFieldOffset() + (info.getStorage().getRecordSize() * y) +
                            info.getStorage().getDataBlockOffset();

                    final int value = info.getBuffer().getInt(offset);

                    if (value == 0)
                        continue;

                    ++checked;

                    if ((value < -FLOAT_CHECK_BOUNDS) || (value > FLOAT_CHECK_BOUNDS))
                        ++match;
                }

                if (checked == 0)
                    return false;

                return (match / checked) >= CHECK_PERCENTAGE;
            })
            .register(ClientStorageFormer.FT_IND, info ->
            {
                return info.getFormat().indexOf(String.valueOf(ClientStorageFormer.FT_IND.getFormer())) == -1;
            });
    }

    private FieldEstimater register(final ClientStorageFormer former,
            final Predicate<CheckInfo> check)
    {
        checks.add(new Pair<>(former, check));
        return this;
    }

    public ClientStorageFormer estimateFormer(final CheckInfo info)
    {
        for (final Pair<ClientStorageFormer, Predicate<CheckInfo>> entry : checks)
            if (entry.second().test(info))
                return entry.first();

        return defaultFormer;
    }
}

public class UnknownSchemaDataTable<T extends ClientStorageStructure>
    extends AbstractDataTable<T>
{
    private final static FieldEstimater ESTIMATER =
            new FieldEstimater(ClientStorageFormer.FT_INT);

    private final Object[][] objects;

    public UnknownSchemaDataTable(final ClientStorage<T> storage, final ByteBuffer buffer)
    {
        super(storage, estimateFormat(storage, buffer));

        objects = new Object[storage.getRecordsCount()][getFormat().length()];

        for (int offset = storage.getDataBlockOffset(), y = 0;
                offset < storage.getStringBlockOffset();
                    offset += storage.getRecordSize(), ++y)
        {
            final ClientStorageRecord record = new ClientStorageRecord(buffer, storage, getFormat(), offset);

            for (int x = 0; x < getFormat().length(); ++x)
            {
                final Object obj;
                switch (getFormat().getFormerAtIndex(x))
                {
                    case FT_INT:
                    case FT_IND:
                        obj = record.getInt(x);
                        break;
                    case FT_FLOAT:
                        obj = record.getFloat(x);
                        break;
                    case FT_STRING:
                        obj = record.getString(x);
                        break;
                    default:
                        obj = null;
                }

                objects[y][x] = obj;
            }
        }
    }

    private static ClientStorageFormat estimateFormat(final ClientStorage<?> storage, final ByteBuffer buffer)
    {
        final StringBuilder format = new StringBuilder();

        // Normalizes size to 4 bytes since we can't estimate single byte columns
        final int normalizedRecordSize = storage.getRecordSize() - (storage.getRecordSize() % Integer.BYTES);

        int offset = 0;
        while (offset < normalizedRecordSize)
        {
            final ClientStorageFormer former = ESTIMATER.estimateFormer(
                    new CheckInfo(storage, buffer, format, offset));

            format.append(former.getFormer());
            offset += former.getSize();
        }

        return new ClientStorageFormat(format.toString(),
                String.format("estimated - %s byte(s) left", storage.getRecordSize() - offset));
    }

    @Override
    public List<String> getFieldNames()
    {
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < getFormat().getFormat().length(); ++i)
            list.add("Column " + i);

        return list;
    }

    @Override
    public List<String> getFieldDescription()
    {
        final List<String> list = new ArrayList<>();
        getFormat().forEach(entry -> list.add(String.format("Estimated %s",
                entry.second().getType().getRawType().getSimpleName())));
        return list;
    }

    @Override
    public List<TypeToken<?>> getFieldType()
    {
        final List<TypeToken<?>> list = new ArrayList<>();
        getFormat().forEach(entry -> list.add(entry.second().getType()));
        return list;
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        throw new UnsupportedOperationException("You can't get specific entries from estimated storages!");
    }

    @Override
    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        return objects;
    }

    @Override
    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException("You can't iterate through estimated storages!");
    }
}
