
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.util.Pair;

public interface StructureChangeTracker
    extends ScopedStructureChange
{
    // Policy decides whether we track a structure detailed or fast forward
    public ReadOnlySetProperty<ServerStorageStructure> structuresRecentlyCreated();

    public ReadOnlySetProperty<ServerStorageStructure> structuresRecentlyDeleted();

    public ReadOnlySetProperty<Pair<ObservableValue<?>, MappingMetaData>> observablesRecentlyChanged();

    public ReadOnlyObjectProperty<Object> remoteValue(ServerStorageStructure structure, Pair<ObservableValue<?>, MappingMetaData> value);

    /**
     * @return Returns all versions of a single structure
     */
    public ReadOnlyListProperty<StructureChangeEvent> getVersionsOfStructure(ServerStorageStructure structure);

    /**
     * Drops all changes so the change tracker is in sync with the remote database
     */
    public void drop();

    // TODO Move this to ServerStorage
    /**
     * Commits the current content to the database
     */
    public void commit();

    /**
     * @return Returns the sql query of all changes
     */
    public String getQuery();
}
