
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.Optional;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;

public final class SQLUpdateInfoImpl implements SQLUpdateInfo
{
    private final ReadOnlyProperty<?> property;

    private final Optional<Object> oldValue;

    public SQLUpdateInfoImpl(final ReadOnlyProperty<?> property)
    {
        this (property, null);
    }

    public SQLUpdateInfoImpl(final ReadOnlyProperty<?> property, final Object oldValue)
    {
        this.property = property;
        this.oldValue = Optional.ofNullable(oldValue);
    }

    @Override
    public ReadOnlyProperty<?> getProperty()
    {
        return property;
    }

    @Override
    public Optional<Object> getOldValue()
    {
        return oldValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SQLUpdateInfoImpl))
            return false;
        final SQLUpdateInfoImpl other = (SQLUpdateInfoImpl) obj;
        if (property == null)
        {
            if (other.property != null)
                return false;
        }
        else if (!property.equals(other.property))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("UpdateInfo(%s -> %s)", property, oldValue);
    }
}
