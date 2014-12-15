
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.lang.annotation.Annotation;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.StorageException;
import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.storage.StorageStructure;

@StorageName(name=".*")
public abstract class ClientStorageStructure extends StorageStructure
{
    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ClientStorageEntry.class;
    }

    public static String getPathThroughStorageName(final Class<? extends ClientStorageStructure> type)
    {
        try
        {
            return getPathOfFile(StorageStructure.getStorageName(type));
        }
        catch (final StorageException e)
        {
            return new String();
        }
    }

    public static String getPathOfFile(final String path)
    {
        return WIde.getConfig().get().getActiveEnviroment().getClientStorageConfig().path().get() + "/" + path;
    }
}
