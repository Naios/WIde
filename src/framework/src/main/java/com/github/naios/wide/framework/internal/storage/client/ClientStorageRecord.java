
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormer;

@SuppressWarnings("serial")
class WrongFormatType extends RuntimeException
{
    public WrongFormatType(final String type, final ClientStorageFormer former, final int index, final ClientStorageFormat format)
    {
        super(String.format("Requested type: %s does not match former: %s at index: %s. Format is %s",
                type, former, index, format));
    }
}

/**
 * Provides safe references to ByteBuffer values mapped through a ClientStorageFormat
 */
public class ClientStorageRecord
{
    private final ByteBuffer buffer;

    private final ClientStorageImpl<?> storage;

    private final int offset;

    private final ClientStorageFormat format;

    public ClientStorageRecord(final ByteBuffer buffer,
            final ClientStorageImpl<?> storage, final ClientStorageFormat format, final int offset)
    {
        this.buffer = buffer;
        this.storage = storage;
        this.offset = offset;
        this.format = format;
    }

    public byte getByte(final int index)
    {
        checkFormer(index, ClientStorageFormer.FT_BYTE);
        return buffer.get(offset + getOffsetSafe(index));
    }

    public int getInt(final int index)
    {
        checkFormer(index, ClientStorageFormer.FT_IND, ClientStorageFormer.FT_INT);
        return buffer.getInt(offset + getOffsetSafe(index));
    }

    public int getInt(final int index, final boolean key)
    {
        if (key)
            checkFormer(index, ClientStorageFormer.FT_IND);
        else
            checkFormer(index, ClientStorageFormer.FT_INT);

        return buffer.getInt(offset + getOffsetSafe(index));
    }

    public long getLong(final int index)
    {
        checkFormer(index, ClientStorageFormer.FT_LONG);
        return buffer.getLong(offset + getOffsetSafe(index));
    }

    public float getFloat(final int index)
    {
        checkFormer(index, ClientStorageFormer.FT_FLOAT);
        return buffer.getFloat(offset + getOffsetSafe(index));
    }

    public String getString(final int index)
    {
        checkFormer(index, ClientStorageFormer.FT_STRING);
        final int position = buffer.getInt(offset + getOffsetSafe(index));
        return storage.getStringTable().getString(position);
    }

    /**
     * Checks if an index matches to one of several formers
     */
    private void checkFormer(final int index, final ClientStorageFormer... formers) throws WrongFormatType
    {
        final ClientStorageFormer former = format.getFormerAtIndex(index);
        final List<ClientStorageFormer> list = Arrays.asList(formers);
        if (!list.contains(former))
            throw new WrongFormatType(list.toString(), former, index, format);
    }

    /**
     * Returns an offset to an index and checks for out of bounds
     */
    private int getOffsetSafe(final int index) throws IndexOutOfBoundsException
    {
        final int offset = format.getOffsetOfIndex(index);

        if (offset > storage.getRecordSize())
            throw new IndexOutOfBoundsException(
                    String.format("Index: %s is out of bounds for format: %s",
                            index, format));

        return offset;
    }
}
