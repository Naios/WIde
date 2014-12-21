package com.github.naios.wide.core.framework.storage.client;

import java.util.Iterator;

public interface ClientStorageDataTable<T extends ClientStorageStructure> extends Iterable<T>
{
    public String[] getFieldName();

    public String[] getFieldDescription();

    public Class<?>[] getFieldType();

    public T getEntry(int entry) throws ClientStorageException;

    public Object[][] asObjectArray(boolean prettyWrap);

    @Override
    public Iterator<T> iterator();
}
