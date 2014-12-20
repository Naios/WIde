
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

public class ClientStorageRecord
{
    private final ClientStorage<?> owner;

    private final int start;

    private int offset;

    public ClientStorageRecord(final ClientStorage<?> owner, final int start)
    {
        this.start = start;
        this.owner = owner;
    }

    public void first()
    {
        offset = 0;
    }

    public byte nextByte()
    {
        checkInBounds();
        final byte result = owner.getByteBuffer().getInt(start + offset);
        offset += Integer.BYTES;
        return result;
    }

    public int nextInt()
    {
        checkInBounds();
        final int result = owner.getByteBuffer().getInt(start + offset);
        offset += Integer.BYTES;
        return result;
    }

    public long nextLong()
    {
        checkInBounds();
        final long result = owner.getByteBuffer().getLong(start + offset);
        offset += Long.BYTES;
        return result;
    }

    public float nextFloat()
    {
        checkInBounds();
        final float result = owner.getByteBuffer().getFloat(start + offset);
        offset += Float.BYTES;
        return result;
    }

    public String nextString()
    {
        checkInBounds();
        final int result = owner.getByteBuffer().getInt(start + offset);
        offset += Integer.BYTES;
        return owner.getStringTable().getString(result);
    }

    private void checkInBounds()
    {

    }

    private void checkBytesLeft()
    {

    }
}
