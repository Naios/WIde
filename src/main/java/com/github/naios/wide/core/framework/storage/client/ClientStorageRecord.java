
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.Arrays;

@SuppressWarnings("serial")
class WrongFormatType extends RuntimeException
{
    public WrongFormatType(final Class<?> type, final ClientStorageFormer former, final int index)
    {
        super(String.format("Requested type: %s does not match former: %s at index: %s",
                type.getClass().getName(), former, index));
    }
}

/**
 * Provides safe references to ByteBuffer values mapped through a ClientStorageFormat
 */
public class ClientStorageRecord
{
    private final ByteBuffer buffer;

    private final ClientStorage<?> storage;

    private final int offset;

    private final ClientStorageFormat format;

    public ClientStorageRecord(final ByteBuffer buffer,
            final ClientStorage<?> storage, final ClientStorageFormat format, final int offset)
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
        if (!Arrays.asList(formers).contains(former))
            throw new WrongFormatType(former.getClass(), former, index);
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
