
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
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public class ClientStorageSelector<T extends ClientStorageStructure>
{
    private static String getPathForStorage(final String path)
    {
        return FrameworkServiceImpl.getConfigService().getActiveEnvironment()
                .getClientStorageConfig().path().get() + "/" + path;
    }

    public static <T extends ClientStorageStructure> ClientStorageImpl<T> select(final String name,
            final ClientStoragePolicy policy) throws ClientStorageException
    {
        // TODO improve this: maybe there is an easier way to get the extension
        final String extension = name.substring(name.lastIndexOf("."), name.length());

        switch (extension)
        {
            case ADBStorage.EXTENSION:
                return new ADBStorage<>(getPathForStorage(name), policy);
            case DB2Storage.EXTENSION:
                return new DB2Storage<>(getPathForStorage(name), policy);
            case DBCStorage.EXTENSION:
                return new DBCStorage<>(getPathForStorage(name), policy);
            default:
                return null;
        }
    }
}
