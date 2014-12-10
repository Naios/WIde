
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.util.Pair;

/**
 * Helps us to split scopes of multiple collections
 */
abstract class Splitter<T> implements Consumer<T>
{
    private final ServerStorageChangeHolder holder;

    private final Map<String, SQLScope> scopes;

    public Splitter(final ServerStorageChangeHolder holder, final Map<String, SQLScope> scopes)
    {
        this.holder = holder;

        this.scopes = scopes;
    }

    @Override
    public void accept(final T entry)
    {
        final String name = getScope(entry);

        SQLScope scope = scopes.get(name);
        if (scope == null)
        {
            scope = new SQLScope();
            scopes.put(name, scope);
        }

        addObservable(scope, entry);
    }

    public abstract String getScope(T entry);

    public abstract void addObservable(SQLScope scope, T entry);
}

public class SQLScope
{
    private final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> update;

    private final Collection<ServerStorageStructure> insert, delete;

    public SQLScope()
    {
        this.update = new ArrayList<Pair<ObservableValue<?>, ObservableValueStorageInfo>>();
        this.insert = new ArrayList<ServerStorageStructure>();
        this.delete = new ArrayList<ServerStorageStructure>();
    }

    public SQLScope(
            final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        this.update = update;
        this.insert = insert;
        this.delete = delete;
    }

    public Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> getUpdate()
    {
        return update;
    }

    public Collection<ServerStorageStructure> getInsert()
    {
        return insert;
    }

    public Collection<ServerStorageStructure> getDelete()
    {
        return delete;
    }

    /**
     * Splits collections containing update, insert & delete structures into its scopes
     */
    public static Map<String, SQLScope> split(final ServerStorageChangeHolder holder,
            final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        final Map<String, SQLScope> scopes = new HashMap<>();

        update.forEach(new Splitter<Pair<ObservableValue<?>, ObservableValueStorageInfo>>(holder, scopes)
        {
            @Override
            public String getScope(
                    final Pair<ObservableValue<?>, ObservableValueStorageInfo> entry)
            {
                return holder.getScopeOfObservable(entry.first());
            }

            @Override
            public void addObservable(final SQLScope scope,
                    final Pair<ObservableValue<?>, ObservableValueStorageInfo> entry)
            {
                scope.update.add(entry);
            }
        });

        insert.forEach(new Splitter<ServerStorageStructure>(holder, scopes)
        {
            @Override
            public String getScope(
                    final ServerStorageStructure entry)
            {
                return holder.getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope,
                    final ServerStorageStructure entry)
            {
                scope.insert.add(entry);
            }
        });

        delete.forEach(new Splitter<ServerStorageStructure>(holder, scopes)
        {
            @Override
            public String getScope(
                    final ServerStorageStructure entry)
            {
                return holder.getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope,
                    final ServerStorageStructure entry)
            {
                scope.delete.add(entry);
            }
        });

        return scopes;
    }
}
