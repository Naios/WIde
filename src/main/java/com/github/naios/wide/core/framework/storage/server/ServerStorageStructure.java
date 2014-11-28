package com.github.naios.wide.core.framework.storage.server;

import java.lang.annotation.Annotation;

import com.github.naios.wide.core.framework.storage.StorageStructure;

public abstract class ServerStorageStructure extends StorageStructure
{
    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ServerStorageEntry.class;
    }
}
