
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public abstract class ClientEnumMetaDataMappingAdapter<T extends ReadOnlyProperty<?>, P>
        extends ClientMetaDataMappingAdapter<T, P>
{
    // FIXME fix this raw class hack
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ClientEnumMetaDataMappingAdapter(final Class type, final Class primitive)
    {
        super(type, primitive);
    }

    protected <T extends Enum<?>> Class<T> getEnum(final MappingMetaData metaData)
    {
        return FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias());
    }
}
