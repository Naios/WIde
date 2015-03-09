/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyBooleanProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;

public interface ServerStorage<T extends ServerStorageStructure>
{
    public String getTableName();

    public String getDatabaseId();

    public ReadOnlyBooleanProperty alive();

    public List<MappingMetaData> getKeysMetaData();

    public List<MappingMetaData> getMetaData();

    public Optional<T> request(ServerStorageKey<T> key);

    public List<T> requestWhere(String where, Object... args);

    public List<T> requestWhere(String where);

    public T create(ServerStorageKey<T> key);

    public ChangeTracker getChangeTracker();
}
