/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.mapping.MappingBean;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapter;

public abstract class ClientMetaDataMappingAdapter<T extends ReadOnlyProperty<?>, P>
        extends MappingAdapter<ClientStorageRecord, ClientStorageStructure, ReadOnlyProperty<?>, T, P>
{
    public ClientMetaDataMappingAdapter(final Class<T> type, final Class<P> primitive)
    {
        super(type, primitive);
    }

    class ClientMappingBeanImpl implements MappingBean<ClientStorageStructure>
    {
        private final ClientStorageStructure to;

        private final MappingMetaData        metaData;

        public ClientMappingBeanImpl(final ClientStorageStructure to,
                final MappingMetaData metaData)
        {
            this.to = to;
            this.metaData = metaData;
        }

        @Override
        public ClientStorageStructure getStructure()
        {
            return to;
        }

        @Override
        public MappingMetaData getMappingMetaData()
        {
            return metaData;
        }

        @Override
        public String toString()
        {
            return String.format("ClientMappingBean()");
        };
    }

    protected MappingBean<ClientStorageStructure> createBean(final ClientStorageStructure to,
            final MappingMetaData metaData)
    {
        return new ClientMappingBeanImpl(to, metaData);
    }
}
