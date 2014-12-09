
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.IdentitySet;

/**
 * Implementation of an SQLBuilder based on storage holders
 */
public class LazySQLBuilder implements SQLBuilder
{
    private final Map<String /*id*/, String /*value*/> variables =
            new HashMap<>();

    private final Collection<ServerStorageStructure> insert =
            new IdentitySet<>();

    private final Collection<ServerStorageStructure> delete =
            new IdentitySet<>();

    private final Collection<ObservableValue<?>> update =
            new IdentitySet<>();

    private final ServerStorageChangeHolder changeholder;

    private final boolean variablize;

    public LazySQLBuilder(final ServerStorageChangeHolder changeholder, final boolean variablize)
    {
        this.changeholder = changeholder;
        this.variablize = variablize;
    }

    private String addVariable(final String id, final Object value)
    {
        return addVariable(id, value, 1);
    }

    private String addVariable(final String id, final Object value, final int run)
    {
        final String svalue = new FormatterWrapper(value, FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString();
        final String sid = (run == 1) ? String.format("@%s", id) : String.format("@%s_V%s", id, run);

        // If the variable is already contained in the variables with a different value rename it to id + "_V" + run
        final String containing_value = variables.get(sid);
        if (containing_value != null)
        {
            if (containing_value.equals(svalue))
                return sid;
            else
                return addVariable(id, value, run + 1);
        }
        else
        {
            variables.put(sid, svalue);
            return sid;
        }
    }

    /**
     * Adds all Observables that has changed since the last database sync
     */
    @Override
    public SQLBuilder addRecentChanged()
    {
        update.addAll(changeholder.getObservablesChanged());
        return this;
    }

    /**
     * Adds all Observables that has changed (ignores database sync)
     */
    @Override
    public SQLBuilder addAllChanged()
    {
        update.addAll(changeholder.getAllObservablesChanged());
        return this;
    }

    /**
     * Adds some Observables to the builder
     */
    @Override
    public SQLBuilder add(final ObservableValue<?>... value)
    {
        update.addAll(Arrays.asList(value));
        return this;
    }

    /**
     * Adds some Structures to the builder to build insert querys
     */
    @Override
    public SQLBuilder addCreate(final ServerStorageStructure... structure)
    {
        insert.addAll(Arrays.asList(structure));
        return this;
    }

    /**
     * Adds some Structures to the builder to build delete querys
     */
    @Override
    public SQLBuilder addDelete(final ServerStorageStructure... structure)
    {
        delete.addAll(Arrays.asList(structure));
        return this;
    }

    /**
     * Clears the builder
     */
    @Override
    public SQLBuilder clear()
    {
        update.clear();
        insert.clear();
        delete.clear();
        return this;
    }

    private void calculateVariables()
    {
    }

    private void calculateInserts()
    {
    }

    private void calculateDeletes()
    {
    }

    private void writeVariables(final StringWriter writer)
    {
        // TODO Sort variables
        for (final Entry<String, String> entry : variables.entrySet())
            writer.write(String.format("SET %s := %s;", entry.getKey(), entry.getValue()));
    }

    private void writeInserts(final StringWriter writer)
    {
    }

    private void writeDeletes(final StringWriter writer)
    {
    }

    /**
     * Builds our SQL query
     */
    @Override
    public void write(final OutputStream stream)
    {
        final StringWriter writer = new StringWriter();

        // Delete all structures contained in delete from create.
        insert.removeAll(delete);

        // Pre calculate stuff
        if (variablize)
            calculateVariables();

        calculateInserts();
        calculateDeletes();

        // Write SQL
        if (variablize)
            writeVariables(writer);

        writeDeletes(writer);
        writeInserts(writer);

        // Iterate through all changed ObservableValues


        // Replace namestorage, enum and flag fields through sql variables

        // Write "Changesets" for all updated ObservableValues & deleted structures

        // Summary update/delete Changesets that target the same table but different keys

        // Write insert querys for all new structures, summary same tables

        try
        {
            writer.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Executes our sql batch on the connection
     */
    @Override
    public boolean commit()
    {
        if (changeholder.connection().get() == null)
            return false;

        try (final Statement statement = changeholder.connection().get().createStatement())
        {
            statement.execute(toString());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(stream);

        return new String(stream.toByteArray());
    }
}
