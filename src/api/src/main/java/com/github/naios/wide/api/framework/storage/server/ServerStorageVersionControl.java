
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface ServerStorageVersionControl
{
    /**
     * @return Our scope property
     */
    public StringProperty scope();

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
    public void setCustomVariable(ObservableValue<?> value, String name);

    /**
     * Releases a custom variable of an observable value
     */
    public void releaseCustomVariable(ObservableValue<?> value);

    /**
     * Gets the custom variable of the observable value
     * @param value The observable value we want to get the variable name of
     * @return null if not existing, variable name otherwise
     */
    public String getCustomVariable(ObservableValue<?> value);

    /**
     * Sets the comment of the current scope
     * @param comment the comment you want to set
     */
    public void setScopeComment(String comment);

    /**
     * Creates an anti change if possible that reverts the version change.<br>
     * Takes care of FlagsProperties, creation and deletion of structures.
     * @param structure The structure you want to revert
     * @param version   The version you want to revert
     * @return Returns true on success
     */
    public boolean revert(ServerStorageStructure structure, ServerStorageVersionStamp version);

    /**
     * Resets all changes until the time of the version
     * @param structure The structure you want to revert
     * @param version   The version you want to revert
     * @return Returns true on success
     */
    public boolean reset(ServerStorageStructure structure, ServerStorageVersionStamp version);

    /**
     * @return Returns all versions of a structure
     */
    public ObservableList<ServerStorageVersion> getVersionsOfStructure();

    /**
     * @return Returns a change map that represent all changes.
     */
    public ServerStorageVersionMap getChangeMap();

    /**
     * Commits the current content to the database
     */
    public void commit();

    /**
     * @return Returns the sql query of all changes
     */
    public String getQuery();
}
