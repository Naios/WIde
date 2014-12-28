
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.nio.ByteBuffer;

import com.github.naios.wide.api.framework.storage.client.ClientStorageException;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;

/**
 * Implementation of Blizzards DB2 files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DB2
 */
public class DB2Storage<T extends ClientStorageStructure> extends ClientStorageImpl<T>
{
    private final static int HEADER_SIZE = 48;

    private final static String MAGIC = "WDB2";

    protected final static String EXTENSION = ".db2";

    private int tableHash, timestampLastWritten, minId, maxId, locale, unk2;

    public DB2Storage(final String path) throws ClientStorageException
    {
        super(path);
    }

    public DB2Storage(final String path, final ClientStoragePolicy policy)
    {
        super(path, policy);
    }

    @Override
    protected void finishHeaderReading(final ByteBuffer buffer)
    {
        tableHash = buffer.getInt();
        timestampLastWritten = buffer.getInt();
        minId = buffer.getInt();
        maxId = buffer.getInt();
        locale = buffer.getInt();
        unk2 = buffer.getInt();
    }

    @Override
    protected int getHeaderSize()
    {
        return HEADER_SIZE;
    }

    @Override
    protected String getMagicSig()
    {
        return MAGIC;
    }

    @Override
    protected String getExtension()
    {
        return EXTENSION;
    }

    public int getTableHash()
    {
        return tableHash;
    }

    public int getMinId()
    {
        return minId;
    }

    public int getMaxId()
    {
        return maxId;
    }

    public int getLocale()
    {
        return locale;
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        // TODO Fix this (some Draenor db2 storages don't declare this field)
        // if ((entry < minId) || (entry > maxId))
            // throw new MissingEntryException();

        return super.getEntry(entry);
    }
}
