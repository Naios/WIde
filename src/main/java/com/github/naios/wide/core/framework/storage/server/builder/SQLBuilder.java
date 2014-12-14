
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.util.Pair;

/**
 * Implementation of an SQLBuilder based on storage holders
 */
public class SQLBuilder
{
    private final Collection<ServerStorageStructure> insert =
            new ArrayList<>();

    private final Collection<ServerStorageStructure> delete =
            new ArrayList<>();

    private final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> update =
            new ArrayList<>();

    private final ServerStorageChangeHolder changeholder;

    private final boolean variablize;

    public SQLBuilder(final ServerStorageChangeHolder changeholder, final boolean variablize)
    {
        this.changeholder = changeholder;
        this.variablize = variablize;
    }

    /**
     * Adds all Observables that has changed since the last database sync
     */
    public SQLBuilder addRecentChanged()
    {
        addUpdates(changeholder.getObservablesChanged());
        return this;
    }

    /**
     * Adds all Observables that has changed (ignores database sync)
     */
    public SQLBuilder addAllChanged()
    {
        addUpdates(changeholder.getAllObservablesChanged());
        return this;
    }

    /**
     * Adds some Observables to the builder
     */
    public SQLBuilder add(final ObservableValue<?>... value)
    {
        addUpdates(Arrays.asList(value));
        return this;
    }

    private void addUpdates(final Collection<ObservableValue<?>> values)
    {
        values.forEach((value) ->
        {
            final ObservableValueStorageInfo info =
                    changeholder.getStorageInformationOfObservable(value);

            // Observable hasn't changed, do nothing
            // TODO maybe we want to add support to build querys from non changed records later
            if (info == null)
                return;

            update.add(new Pair<>(value, info));
        });
    }

    /**
     * Adds some Structures to the builder to build insert querys
     */
    public SQLBuilder addCreate(final ServerStorageStructure... structure)
    {
        return addCreate(Arrays.asList(structure));
    }

    /**
     * Adds some Structures to the builder to build insert querys
     */
    public SQLBuilder addCreate(final Collection<ServerStorageStructure> structures)
    {
        insert.addAll(structures);
        return this;
    }

    /**
     * Adds some Structures to the builder to build delete querys
     */
    public SQLBuilder addDelete(final ServerStorageStructure... structure)
    {
        return addDelete(Arrays.asList(structure));
    }

    /**
     * Adds some Structures to the builder to build insert querys
     */
    public SQLBuilder addDelete(final Collection<ServerStorageStructure> structures)
    {
        delete.addAll(structures);
        return this;
    }

    /**
     * Clears the builder
     */
    public SQLBuilder clear()
    {
        update.clear();
        insert.clear();
        delete.clear();
        return this;
    }

    /**
     * Cleans our storage & value collections of invalid entrys
     */
    private void cleanStores()
    {
        // Delete all structures contained in delete from create.
        insert.removeAll(delete);

        // Delete all values from update querys that are inserted in insert statements anyway
        update.removeIf((value) ->
        {
            final ObservableValueStorageInfo info = value.second();
            return insert.contains(info.getStructure()) ||
                   // value should never exist in delete structures but we handle it.
                   delete.contains(info.getStructure());
        });
    }

    /**
     * Builds our SQL query
     */
    public void write(final OutputStream stream)
    {
        final PrintWriter writer = new PrintWriter(stream);

        // Clean our stores of bad entrys
        cleanStores();

        // Pre calculate everything
        final SQLVariableHolder vars = new SQLVariableHolder();
        final Map<String /*scope*/, SQLScope> scopes = SQLScope.split(changeholder, update, insert, delete);
        final Map<String /*scope*/, String /*query*/> querys = new HashMap<>();

        for (final Entry<String, SQLScope> entry : scopes.entrySet())
            querys.put(entry.getKey(), entry.getValue().buildQuery(entry.getKey(), vars, changeholder, variablize));

        vars.writeQuery(writer);

        for (final Entry<String, String> entry : querys.entrySet())
        {
            final String comment = changeholder.getScopeComment(entry.getKey());

            if (!comment.isEmpty())
                writer.println(SQLMaker.createComment(comment));

            // The actual scope querys
            writer.println(entry.getValue());
        }

        writer.close();
    }

    /**
     * Executes our sql batch on the connection
     */
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
        return new String(stream.toByteArray()).trim();
    }
}
