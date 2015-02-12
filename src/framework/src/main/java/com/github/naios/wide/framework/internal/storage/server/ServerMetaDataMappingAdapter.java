/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.sql.ResultSet;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerMappingBean;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapter;

public abstract class ServerMetaDataMappingAdapter<T extends ReadOnlyProperty<?>, P>
        extends MappingAdapter<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>, T, P>
{
    public ServerMetaDataMappingAdapter(final Class<T> type, final Class<P> primitive)
    {
        super(type, primitive);
    }

    class ServerMappingBeanImpl implements ServerMappingBean
    {
        private final ServerStorageStructure to;

        private final MappingMetaData        metaData;

        public ServerMappingBeanImpl(final ServerStorageStructure to,
                final MappingMetaData metaData)
        {
            this.to = to;
            this.metaData = metaData;
        }

        @Override
        public ServerStorageStructure getStructure()
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
            return String.format("ServerMappingBean(%s)", to.getOwner().getTableName());
        };
    }

    protected ServerMappingBean createBean(final ServerStorageStructure to,
            final MappingMetaData metaData)
    {
        return new ServerMappingBeanImpl(to, metaData);
    }
}
