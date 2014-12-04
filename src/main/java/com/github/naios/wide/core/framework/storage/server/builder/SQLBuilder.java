package com.github.naios.wide.core.framework.storage.server.builder;

import java.util.Arrays;
import java.util.Collection;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.util.IdentitySet;

/**
 * Main Class to build sql files from Changes hold in the ChangeHolder
 */
public class SQLBuilder
{
    private static final SQLBuilder INSTANCE = new SQLBuilder();

    private final Collection<ObservableValue<?>> values =
            new IdentitySet<>();

    public SQLBuilder instance()
    {
        return INSTANCE;
    }

    /**
     * Adds all Observables that has changed since the last database sync
     */
    public SQLBuilder addRecentChanged()
    {
        values.addAll(ServerStorageChangeHolder.instance().getObservablesChanged(true));
        return this;
    }

    /**
     * Adds all Observables that has changed (ignores database sync)
     */
    public SQLBuilder addAllChanged()
    {
        values.addAll(ServerStorageChangeHolder.instance().getObservablesChanged(false));
        return this;
    }

    /**
     * Adds some Observables to the builder
     */
    public SQLBuilder add(final ObservableValue<?>... value)
    {
        values.addAll(Arrays.asList(value));
        return this;
    }

    /**
     * Clears the builder
     */
    public SQLBuilder clear()
    {
        values.clear();
        return this;
    }

    /**
     * Builds our SQL query
     */
    public String build()
    {
        final StringBuilder builder = new StringBuilder();

        // ServerStorageChangeHolder.instance()

        // Iterate through all changed ObservableValues

        // Replace namestorage, enum and flag fields through sql variables

        // Write "Changesets" for all updated ObservableValues & deleted structures

        // Summary update/delete Changesets that target the same table but different keys

        // Write insert querys for all new structures, summary same tables

        return builder.toString();
    }
}
