
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ReadOnlyListProperty;

public interface VersionedStructureChangeTracker
    extends StructureChangeTracker
{
    /**
     * Resets all changes until the time where the event occurred
     * @param structure The structure you want to revert
     * @param event     The Event where you want to revert to
     */
    public void reset(ServerStorageStructure structure, StructureChangeEvent event);

    /**
     * @return Returns all versions of a single structure
     */
    public ReadOnlyListProperty<StructureChangeEvent> getVersionsOfStructure(ServerStorageStructure structure);
}
