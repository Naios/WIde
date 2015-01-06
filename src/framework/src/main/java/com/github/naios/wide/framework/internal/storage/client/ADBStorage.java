
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import com.github.naios.wide.api.framework.storage.client.ClientStorageException;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;

/**
 * Implementation of Blizzards ADB Cache files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=ADB
 */
public class ADBStorage<T extends ClientStorageStructure> extends DB2Storage<T>
{
    private final static String MAGIC = "WCH2";

    protected final static String EXTENSION = ".adb";

    public ADBStorage(final String path) throws ClientStorageException
    {
        super(path);
    }

    public ADBStorage(final String path, final ClientStoragePolicy policy)
    {
        super(path, policy);
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
}
