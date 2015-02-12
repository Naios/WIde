package com.github.naios.wide.framework.internal.storage.client;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.naios.wide.api.framework.storage.client.ClientStorageException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.google.common.reflect.TypeToken;

public interface ClientStorageDataTable<T extends ClientStorageStructure> extends Iterable<T>
{
    public List<String> getFieldNames();

    public List<String> getFieldDescription();

    public List<TypeToken<?>> getFieldType();

    public Optional<T> getEntry(int entry) throws ClientStorageException;

    public Object[][] asObjectArray();

    public ClientStorageFormat getFormat();

    @Override
    public Iterator<T> iterator();
}
