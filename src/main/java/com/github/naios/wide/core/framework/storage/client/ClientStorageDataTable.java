package com.github.naios.wide.core.framework.storage.client;

import java.util.Iterator;
import java.util.List;

import com.google.common.reflect.TypeToken;

public interface ClientStorageDataTable<T extends ClientStorageStructure> extends Iterable<T>
{
    public List<String> getFieldNames();

    public List<String> getFieldDescription();

    public List<TypeToken<?>> getFieldType();

    public T getEntry(int entry) throws ClientStorageException;

    public Object[][] asObjectArray();

    public ClientStorageFormat getFormat();

    @Override
    public Iterator<T> iterator();
}
