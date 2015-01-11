
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.util.Pair;

public interface StructureChangeTracker
    extends ScopedStructureChange
{
    public void onInsert(final ServerStorageStructure storage, final ObservableValue<?> observable, final Object oldValue);

    public void onCreate(final ServerStorageStructure storage);

    public void onDelete(final ServerStorageStructure storage);

    public ReadOnlyListProperty<ServerStorageStructure> getAllStructuresCreated();

    public ReadOnlyListProperty<ServerStorageStructure> getAllStructuresDeleted();

    public ReadOnlyListProperty<Pair<ObservableValue<?>, MappingMetaData>> getAllObservablesChanged();

    public Object getValueAtRemote(ServerStorageStructure structure, Pair<ObservableValue<?>, MappingMetaData> entry);

    /**
     * Commits the current content to the database
     */
    public void commit();

    /**
     * @return Returns the sql query of all changes
     */
    public String getQuery();
}
