package com.github.naios.wide.core.framework.storage.client;

import com.github.naios.wide.core.framework.storage.StorageException;

/**
 * Implementation of Blizzards ADB Cache files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=ADB
 */
public class ADBStorage<T extends ClientStorageStructure> extends DB2Storage<T>
{
    private final static String MAGIC = "WCH2";

    protected final static String EXTENSION = ".adb";

    public ADBStorage(final Class<? extends ClientStorageStructure> type) throws StorageException
    {
        super(type);
    }

    public ADBStorage(final Class<? extends ClientStorageStructure> type, final String path) throws ClientStorageException
    {
        super(type, path);
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
