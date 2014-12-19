
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;


/**
 * Implementation of Blizzards DBC files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DBC
 */
public class DBCStorage<T extends ClientStorageStructure> extends ClientStorage<T>
{
    private final static int HEADER_SIZE = 20;

    private final static String MAGIC = "WDBC";

    protected final static String EXTENSION = ".dbc";

    public DBCStorage(final String path) throws ClientStorageException
    {
        super(path);
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
}
