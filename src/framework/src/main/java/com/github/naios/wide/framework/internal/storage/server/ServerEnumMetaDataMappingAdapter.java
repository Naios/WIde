
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public abstract class ServerEnumMetaDataMappingAdapter<T extends ReadOnlyProperty<?>, P>
        extends ServerMetaDataMappingAdapter<T, P>
{
    // FIXME fix this raw class hack
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ServerEnumMetaDataMappingAdapter(final Class type, final Class primitive)
    {
        super(type, primitive);
    }

    protected <E extends Enum<?>> Class<E> getEnum(final MappingMetaData metaData)
    {
        return FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias());
    }
}
