
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.Optional;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.util.Pair;

public final class SQLUpdateInfoImpl implements SQLUpdateInfo
{
    private final Pair<ObservableValue<?>, MappingMetaData> entry;

    private final Optional<Object> oldValue;

    public SQLUpdateInfoImpl(final Pair<ObservableValue<?>, MappingMetaData> entry)
    {
        this (entry, null);
    }

    public SQLUpdateInfoImpl(final Pair<ObservableValue<?>, MappingMetaData> entry,
            final Object oldValue)
    {
        this.entry = entry;
        this.oldValue = Optional.ofNullable(oldValue);
    }

    @Override
    public Pair<ObservableValue<?>, MappingMetaData> getEntry()
    {
        return entry;
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
        result = prime * result + ((entry == null) ? 0 : entry.hashCode());
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
        if (entry == null)
        {
            if (other.entry != null)
                return false;
        }
        else if (!entry.equals(other.entry))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("UpdateInfo(%s -> %s)", entry, oldValue);
    }
}
