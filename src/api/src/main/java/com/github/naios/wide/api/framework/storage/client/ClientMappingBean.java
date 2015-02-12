
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.client;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.MappingBean;

public interface ClientMappingBean extends MappingBean<ClientStorageStructure>
{
    public static ClientMappingBean get(final ReadOnlyProperty<?> property)
    {
        return (ClientMappingBean)property.getBean();
    }

    public static ClientStorageStructure getStructure(final ReadOnlyProperty<?> property)
    {
        return get(property).getStructure();
    }

    public static MappingMetaData getMetaData(final ReadOnlyProperty<?> property)
    {
        return get(property).getMappingMetaData();
    }
}
