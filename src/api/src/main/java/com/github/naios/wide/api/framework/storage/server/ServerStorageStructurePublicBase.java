
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ServerStorageStructurePublicBase
{
    public ServerStorage<?> getOwner();

    public ReadOnlyObjectProperty<StructureState> state();

    public ObjectProperty<UpdatePolicy> updatePolicy();

    /**
     * Resets all non-key values to its default value
     */
    public void reset();

    /**
     * Deletes the structure in the database
     */
    public void delete();

    /**
     * Release all references as soon as the structure is in sync with the database<br>
     * This will drop the history
     */
    public void release();

    /**
     * Rolls back all changes until the time where the event occurred
     * @param event The Event where you want to revert to
     */
    public void rollback(StructureChangeEvent event);

    /**
     * @return Returns the history of the structure
     */
    public ReadOnlyListProperty<StructureChangeEvent> history();
}
