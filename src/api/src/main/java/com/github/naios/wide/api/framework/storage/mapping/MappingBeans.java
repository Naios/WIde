
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.mapping;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;

public class MappingBeans
{
    private MappingBeans() { }

    @SuppressWarnings("unchecked")
    public static <T extends Mapping<?>> MappingBean<T> get(final ReadOnlyProperty<?> property)
    {
        return (MappingBean<T>) property.getBean();
    }

    public static MappingMetaData getMetaData(final ReadOnlyProperty<?> property)
    {
        return get(property).getMappingMetaData();
    }

    public static <T extends Mapping<?>> T getStructure(final ReadOnlyProperty<?> property)
    {
        return MappingBeans.<T>get(property).getStructure();
    }
}
