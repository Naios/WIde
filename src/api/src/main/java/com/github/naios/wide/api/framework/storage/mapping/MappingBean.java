
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.mapping;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.client.ClientMappingBean;

public interface MappingBean<T>
{
    public T getStructure();

    public MappingMetaData getMappingMetaData();

    public static MappingBean<?> get(final ReadOnlyProperty<?> property)
    {
        return (ClientMappingBean)property.getBean();
    }

    public static MappingMetaData getMetaData(final ReadOnlyProperty<?> property)
    {
        return get(property).getMappingMetaData();
    }
}
