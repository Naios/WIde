
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.util.Objects;
import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public class SQLUpdateInfoImpl implements SQLUpdateInfo
{
    private final ReadOnlyProperty<?> property;

    private final ObjectProperty<Optional<Object>> oldValue;

    public SQLUpdateInfoImpl(final ReadOnlyProperty<?> property)
    {
        this.property = Objects.requireNonNull(property);
        this.oldValue = new SimpleObjectProperty<>(Optional.empty());
    }

    public SQLUpdateInfoImpl(final ReadOnlyProperty<?> property, final Object oldValue)
    {
        this.property = Objects.requireNonNull(property);
        this.oldValue = new SimpleObjectProperty<>(Optional.ofNullable(oldValue));
    }

    @Override
    public ReadOnlyProperty<?> getProperty()
    {
        return property;
    }

    @Override
    public ObjectProperty<Optional<Object>> oldValueProperty()
    {
        return oldValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;
        result = prime * result + property.getName().hashCode();
        result = prime * result + MappingBeans.getStructure(property).hashCode();

        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SQLUpdateInfo))
            return false;

        final SQLUpdateInfo other = (SQLUpdateInfo)obj;

        final ServerStorageStructure leftStructure = MappingBeans.getStructure(property);
        final ServerStorageStructure rightStructure = MappingBeans.getStructure(other.getProperty());

        return leftStructure.equals(rightStructure) && other.getProperty().getName().equals(getProperty().getName());
    }
}
