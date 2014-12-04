package com.github.naios.wide.core.framework.storage.client;

import java.lang.annotation.Annotation;

import com.github.naios.wide.core.Constants;
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

    public static String getPathThroughStorageName(final Class<? extends ClientStorageStructure> type) throws StorageException
    {
        return getPathOfFile(StorageStructure.getStorageName(type));
    }

    public static String getPathOfFile(final String path) throws StorageException
    {
        return WIde.getConfig().getProperty(Constants.PROPERTY_DIR_DBC).get() + "/" + path;
    }
}
