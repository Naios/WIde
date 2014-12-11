
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.framework.storage.server.ServerStorageChangeHolder;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.util.Pair;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SQLScope
{
    private final Multimap<ServerStorage<?>, Pair<ObservableValue<?>, ObservableValueStorageInfo>> update = HashMultimap.create();

    private final Multimap<ServerStorage<?>, ServerStorageStructure> insert = HashMultimap.create(), delete = HashMultimap.create();

    public SQLScope()
    {
    }

    public Multimap<ServerStorage<?>, Pair<ObservableValue<?>, ObservableValueStorageInfo>> getUpdate()
    {
        return update;
    }

    public Multimap<ServerStorage<?>, ServerStorageStructure> getInsert()
    {
        return insert;
    }

    public Multimap<ServerStorage<?>, ServerStorageStructure> getDelete()
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

        update.forEach(new SQLScopeSplitter<Pair<ObservableValue<?>, ObservableValueStorageInfo>>(holder, scopes)
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
                scope.update.put(entry.second().getStructure().getOwner(), entry);
            }
        });

        insert.forEach(new SQLScopeSplitter<ServerStorageStructure>(holder, scopes)
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
                scope.insert.put(entry.getOwner(), entry);
            }
        });

        delete.forEach(new SQLScopeSplitter<ServerStorageStructure>(holder, scopes)
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
                scope.delete.put(entry.getOwner(), entry);
            }
        });

        return scopes;
    }

    public String buildQuery(final String key, final SQLVariableHolder vars)
    {
        return "not implemented query";
    }
}
