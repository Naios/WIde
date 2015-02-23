/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.framework.storage.mapping.MappingBean;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

class StructureEntryStorageIndex
{
    private final ReadOnlyProperty<?> property;

    private final MappingBean<ServerStorageStructure> bean;

    public StructureEntryStorageIndex(final ReadOnlyProperty<?> property)
    {
        this.property = property;

        bean = MappingBeans.<ServerStorageStructure>get(property);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bean.getStructure() == null) ? 0 : bean.getStructure().hashCode());
        result = prime * result + ((bean.getMappingMetaData() == null) ? 0 : bean.getMappingMetaData().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof StructureEntryStorageIndex))
            return false;
        final StructureEntryStorageIndex other = (StructureEntryStorageIndex) obj;
        if (property == null)
        {
            if (other.property != null)
                return false;
        }
        else if (!bean.getMappingMetaData().equals(other.bean.getMappingMetaData()))
            return false;
        if (bean.getStructure() == null)
        {
            if (other.bean.getStructure() != null)
                return false;
        }
        else if (!bean.getStructure().equals(other.bean.getStructure()))
            return false;
        return true;
    }
}