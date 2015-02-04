
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.util.Pair;

public class SQLUpdateInfoImpl implements SQLUpdateInfo
{
    private final Pair<ObservableValue<?>, MappingMetaData> entry;

    private final Object oldValue;

    public SQLUpdateInfoImpl(final Pair<ObservableValue<?>, MappingMetaData> entry,
            final Object oldValue)
    {
        this.entry = entry;
        this.oldValue = oldValue;
    }

    @Override
    public Pair<ObservableValue<?>, MappingMetaData> getEntry()
    {
        return entry;
    }

    @Override
    public Object getOldValue()
    {
        return oldValue;
    }
}
