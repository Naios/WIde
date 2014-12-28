/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.storage.client;

import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.framework.internal.storage.client.ClientStorageFormatImpl;
import com.google.common.reflect.TypeToken;

public interface ClientStorage<T extends ClientStorageStructure>
{
    public int getRecordsCount();

    public int getFieldsCount();

    public List<String> getFieldNames();

    public List<String> getFieldDescription();

    public List<TypeToken<?>> getFieldType();

    public T getEntry(int entry) throws ClientStorageException;

    public ClientStorageFormatImpl getFormat();

    /**
     * @return ClientStorage as Object Array (use toString() to get Content)
     */
    public Object[][] asObjectArray();

    /**
     * @return ClientStorage as String Array
     */
    public String[][] asStringArray();

    public Iterator<T> iterator();
}
