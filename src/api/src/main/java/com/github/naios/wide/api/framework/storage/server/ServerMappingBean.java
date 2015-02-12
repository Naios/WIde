
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.MappingBean;

public interface ServerMappingBean extends MappingBean<ServerStorageStructure>
{
    public static ServerMappingBean get(final ReadOnlyProperty<?> property)
    {
        return (ServerMappingBean)property.getBean();
    }

    public static ServerStorageStructure getStructure(final ReadOnlyProperty<?> property)
    {
        return get(property).getStructure();
    }

    public static MappingMetaData getMetaData(final ReadOnlyProperty<?> property)
    {
        return get(property).getMappingMetaData();
    }
}
