
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
    private final String path;

    private final ClientStoragePolicy policy;

    public ClientStorageSelector(final String path)
    {
        this (path, ClientStoragePolicy.DEFAULT_POLICY);
    }

    public ClientStorageSelector(final String name,
            final ClientStoragePolicy policy)
    {
        this.path = getPathForStorage(name);

        this.policy = policy;
    }

    private static String getPathForStorage(final String path)
    {
        return FrameworkServiceImpl.getConfigService().getActiveEnviroment()
                .getClientStorageConfig().path().get() + "/" + path;
    }

    public ClientStorageImpl<T> select() throws ClientStorageException
    {
        // TODO improve this: maybe there is an easier way to get the extension
        final String extension = path.substring(path.lastIndexOf("."), path.length());

        switch (extension)
        {
            case ADBStorage.EXTENSION:
                return new ADBStorage<T>(path, policy);
            case DB2Storage.EXTENSION:
                return new DB2Storage<T>(path, policy);
            case DBCStorage.EXTENSION:
                return new DBCStorage<T>(path, policy);
            default:
                return null;
        }
    }
}
