
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import java.util.Collection;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * Most of this methods are deprecated and will be changed in the near future!
 */
public interface ServerStorageChangeHolder extends Observable
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
     * Cleans up the history to the last database sync
     */
    @Deprecated
    public void free();

    /**
     * Clears the history
     */
    @Deprecated
    public void clear();

    /**
     * Reverts structure <b>hard</b> to last state.<br>
     * Recovers deleted structures!<br>
     * <b>Will erase all changes made on the structure.</b>
     * @param observable value you want to edit.
     */
    @Deprecated
    public void revert(ServerStorageStructure structure);

    /**
     * Resets all changes until the point you started the application
     * @param structure value you want to edit.
     */
    @Deprecated
    public void drop(ServerStorageStructure structure);

    /**
     * Trys to reset all changes until the point you started the application
     * <b>may be unsuccessful if the structure was deleted & inserted</b>
     * @param structure value you want to edit.
     */
    @Deprecated
    public void tryReset(ObservableValue<?> observable);

    /**
     * Drop all changes, made since the last sync.<br>
     * @param observable value you want to edit.
     */
    @Deprecated
    public void drop(ObservableValue<?> observable);

    /**
     * Reverts the last change made
     * @param observable value you want to edit.
     */
    @Deprecated
    public void rollback(ObservableValue<?> observable);

    /**
     * Rolls {@link times} operations back.
     * You cant't roll back behind insert/deletes
     * @param observable The Observable value you want to edit.
     * @param times How many operations you want to roll back.
     */
    @Deprecated
    public void rollback(ObservableValue<?> observable, int times);

    /**
     * @return Returns the latest known state of an observable value
     */
    @Deprecated
    public StructureState getObservablesLatestState(
            ObservableValue<?> observable);

    /**
     * @return All Observables that have changed
     */
    @Deprecated
    public Collection<ObservableValue<?>> getAllObservablesChanged();

    /**
     * @return All Observables that have changed since the last sync
     */
    @Deprecated
    public Collection<ObservableValue<?>> getObservablesChanged();

    /**
     * @return All Structures that have changed
     */
    @Deprecated
    public Collection<ServerStorageStructure> getAllStructuresChanged();

    /**
     * Commits all changes to the database
     */
    public void commit();

    public String getQuery();

    /**
     * Returns the whole history of an ObservableValue
     * @param value The ObservableValue you want to get the history
     * @return An array containing all versioned objects.
     */
    @Deprecated
    public Object[] getHistory(ObservableValue<?> value);
}
