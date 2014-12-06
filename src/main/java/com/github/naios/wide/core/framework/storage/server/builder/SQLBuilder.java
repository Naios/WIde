package com.github.naios.wide.core.framework.storage.server.builder;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.util.IdentitySet;

/**
 * Main Class to build sql files from Changes hold in the ChangeHolder
 */
public class SQLBuilder
{
    private final Collection<ObservableValue<?>> updateValues =
            new IdentitySet<>();

    private final Collection<ServerStorageStructure> createStructures =
            new IdentitySet<>();

    private final Collection<ServerStorageStructure> deleteStructures =
            new IdentitySet<>();

    private final Connection connection;

    public SQLBuilder()
    {
        this(null);
    }

    public SQLBuilder(final Connection connection)
    {
        this.connection = connection;
    }

    /**
     * Adds all Observables that has changed since the last database sync
     */
    public SQLBuilder addRecentChanged()
    {
        updateValues.addAll(ServerStorageChangeHolder.instance().getObservablesChanged());
        return this;
    }

    /**
     * Adds all Observables that has changed (ignores database sync)
     */
    public SQLBuilder addAllChanged()
    {
        updateValues.addAll(ServerStorageChangeHolder.instance().getAllObservablesChanged());
        return this;
    }

    /**
     * Adds some Observables to the builder
     */
    public SQLBuilder add(final ObservableValue<?>... value)
    {
        updateValues.addAll(Arrays.asList(value));
        return this;
    }

    /**
     * Adds some Structures to the builder to build insert querys
     */
    public SQLBuilder addCreate(final ServerStorageStructure... structure)
    {
        createStructures.addAll(Arrays.asList(structure));
        return this;
    }

    /**
     * Adds some Structures to the builder to build delete querys
     */
    public SQLBuilder addDelete(final ServerStorageStructure... structure)
    {
        deleteStructures.addAll(Arrays.asList(structure));
        return this;
    }

    /**
     * Clears the builder
     */
    public SQLBuilder clear()
    {
        updateValues.clear();
        return this;
    }

    /**
     * Builds our SQL query
     */
    public String build()
    {
        final StringBuilder builder = new StringBuilder();

        // Delete all structures contained in delete from create.
        createStructures.removeAll(deleteStructures);





        // ServerStorageChangeHolder.instance()

        // Iterate through all changed ObservableValues

        // Replace namestorage, enum and flag fields through sql variables

        // Write "Changesets" for all updated ObservableValues & deleted structures

        // Summary update/delete Changesets that target the same table but different keys

        // Write insert querys for all new structures, summary same tables

        return builder.toString();
    }

    /**
     * Executes our sql batch on the connection
     */
    public boolean commit()
    {
        if (connection == null)
            return false;

        try (final Statement statement = connection.createStatement())
        {
            statement.execute(build());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
