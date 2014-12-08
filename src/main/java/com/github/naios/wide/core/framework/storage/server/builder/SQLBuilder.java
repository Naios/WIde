package com.github.naios.wide.core.framework.storage.server.builder;

import java.io.OutputStream;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public interface SQLBuilder
{
    /**
     * Adds all Observables that has changed since the last database sync
     */
    public SQLBuilder addRecentChanged();

    /**
     * Adds all Observables that has changed (ignores database sync)
     */
    public SQLBuilder addAllChanged();

    /**
     * Adds some Observables to the builder
     */
    public SQLBuilder add(final ObservableValue<?>... value);

    /**
     * Adds some Structures to the builder to build insert querys
     */
    public SQLBuilder addCreate(final ServerStorageStructure... structure);

    /**
     * Adds some Structures to the builder to build delete querys
     */
    public SQLBuilder addDelete(final ServerStorageStructure... structure);

    /**
     * Clears the builder
     */
    public SQLBuilder clear();

    /**
     * Builds our SQL query
     */
    public void write(OutputStream stream);

    /**
     * Executes our sql batch on the connection
     * @return true on success
     */
    public boolean commit();
    
    /**
     * @return The query
     */
    public String toString();
}
