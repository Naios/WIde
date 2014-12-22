
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.util.Arrays;
import java.util.Objects;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;

public abstract class AbstractDataTable<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>
{
    private final ClientStorage<T> storage;

    private final ClientStorageFormat format;

    public AbstractDataTable(final ClientStorage<T> storage, final ClientStorageFormat format)
    {
        this.storage = storage;

        Objects.requireNonNull(format);
        this.format = format;
    }

    public ClientStorage<T> getStorage()
    {
        return storage;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Mapper<ClientStorageRecord, ClientStorageStructure, ObservableValue<?>>
        createMapper(final TableSchema schema)
    {
        return new JsonMapper(schema, ClientStorageRecordToPropertyMappingAdapterHolder.INSTANCE,
                            Arrays.asList(ClientStoragePrivateBase.class),
                                ClientStorageBaseImplementation.class);
    }

    @Override
    public ClientStorageFormat getFormat()
    {
        return format;
    }
}
