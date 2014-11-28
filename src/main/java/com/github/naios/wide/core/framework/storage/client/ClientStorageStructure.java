package com.github.naios.wide.core.framework.storage.client;

import java.lang.annotation.Annotation;

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
}
