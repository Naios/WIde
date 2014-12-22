
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import com.google.common.reflect.TypeToken;

public class UnknownSchemaDataTable<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>
{
    private final ClientStorage<T> storage;

    private final ByteBuffer buffer;

    public UnknownSchemaDataTable(final ClientStorage<T> storage, final ByteBuffer buffer)
    {
        this.storage = storage;

        this.buffer = buffer;


    }

    @Override
    public List<String> getFieldNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getFieldDescription()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TypeToken<?>> getFieldType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<T> iterator()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
