
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.github.naios.wide.api.framework.storage.client.ClientStorageException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormer;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.util.Pair;

class CheckInfo
{
    private final ClientStorageImpl<?> storage;

    private final ByteBuffer buffer;

    private final StringBuilder format;

    private final int offset;

    public CheckInfo(final ClientStorageImpl<?> storage, final ByteBuffer buffer,
            final StringBuilder format, final int offset)
    {
        this.storage = storage;
        this.format = format;
        this.buffer = buffer;
        this.offset = offset;
    }

    public ClientStorageImpl<?> getStorage()
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

    public UnknownSchemaDataTable(final ClientStorageImpl<T> storage, final ByteBuffer buffer)
    {
        super(storage, buffer, estimateFormat(storage, buffer));
    }

    private static ClientStorageFormatImpl estimateFormat(final ClientStorageImpl<?> storage, final ByteBuffer buffer)
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

        return new ClientStorageFormatImpl(format.toString(),
                String.format("estimated - %s byte(s) left", storage.getRecordSize() - offset));
    }

    @Override
    public List<String> getFieldNames()
    {
        final List<String> list = new ArrayList<>();
        getFormat().forEach(entry -> list.add("Column " + entry.first()));
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
    public T getEntry(final int entry) throws ClientStorageException
    {
        throw new UnsupportedOperationException("You can't get specific entries from estimated storages!");
    }

    @Override
    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException("You can't iterate through estimated storages!");
    }
}
