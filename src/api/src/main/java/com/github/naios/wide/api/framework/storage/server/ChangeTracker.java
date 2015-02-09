
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.StringProperty;

public interface ChangeTracker
    extends SQLInfoProvider
{
    public ReadOnlySetProperty<ServerStorageStructure> structuresCreated();

    public ReadOnlySetProperty<ServerStorageStructure> structuresDeleted();

    public ReadOnlyMapProperty<ServerStorageStructure, ReadOnlySetProperty<SQLUpdateInfo>> entriesChanged();

    /**
     * Planned but not supported yet!
     * @return
     */
    // public ReadOnlyMapProperty<ServerStorage<?>, ReadOnlyMapProperty<ServerStorageStructure, ReadOnlyListProperty<StructureChangeEvent>>> changeMap();

    /**
     * @return Our scope property
     */
    public StringProperty scope();

    /**
     * @return Returns a boolean that identifies if a scope was set.
     */
    public boolean hasScopeSet();

    /**
     * Sets our current scope
     * @param scope unique Scope identifier
     */
    public void setScope(String scope);

    /**
     * Sets our current scope
     * @param scope unique Scope identifier
     * @param comment our comment we want to set
     */
    public void setScope(String scope, String comment);

    /**
     * Releases the scope<br>
     * Equal to setScope(DEFAULT_SCOPE)
     */
    public void releaseScope();

    /**
     * Sets an observable value as custom variable<br>
     * Value is wrapped into the variable then
     */
    public void setCustomVariable(ServerStorageStructure structure, ReadOnlyProperty<?> observable, String name);

    /**
     * Releases a custom variable of an observable value
     */
    public void releaseCustomVariable(ServerStorageStructure structure, ReadOnlyProperty<?> observable);

    /**
     * Sets the comment of the current scope
     * @param comment the comment you want to set
     */
    public void setScopeComment(String comment);

    /**
     * Drops all changes so the change tracker is in sync with the remote database
     */
    public void reset();

    /**
     * Commits the current content to the database
     */
    public void commit();

    /**
     * @return Returns the sql query of all changes
     */
    public String getQuery();
}
