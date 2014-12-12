
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
import java.util.Map.Entry;

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

    protected SQLScope()
    {
    }

    protected Multimap<ServerStorage<?>, Pair<ObservableValue<?>, ObservableValueStorageInfo>> getUpdate()
    {
        return update;
    }

    protected Multimap<ServerStorage<?>, ServerStorageStructure> getInsert()
    {
        return insert;
    }

    protected Multimap<ServerStorage<?>, ServerStorageStructure> getDelete()
    {
        return delete;
    }

    /**
     * Splits collections containing update, insert & delete structures into its scopes
     */
    protected static Map<String, SQLScope> split(final ServerStorageChangeHolder holder,
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

    protected String buildQuery(final String key, final SQLVariableHolder vars,  final ServerStorageChangeHolder changeHolder, final boolean variablize)
    {
        final StringBuilder builder = new StringBuilder();

        for (final Entry<ServerStorage<?>, Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>>> structure : update.asMap().entrySet())
            buildUpdates(builder, changeHolder, structure.getValue(), vars, variablize);

        return builder.toString();
    }

    private void buildUpdates(final StringBuilder builder,  final ServerStorageChangeHolder changeHolder, final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> values,
            final SQLVariableHolder vars, final boolean variablize)
    {
        // TODO Group updates by key or updates
        final Multimap<ServerStorageStructure, Pair<ObservableValue<?>, ObservableValueStorageInfo>> observableGroup = HashMultimap.create();
        values.forEach((value) ->
        {
            observableGroup.put(value.second().getStructure(), value);
        });

        final Multimap<String /*changes*/, ServerStorageStructure> changesPerStructure = HashMultimap.create();

        // Build Changeset (updates without key)
        for (final Entry<ServerStorageStructure, Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>>> entry
                : observableGroup.asMap().entrySet())
            changesPerStructure.put(SQLMaker.createUpdateFields(vars, changeHolder, entry.getValue()), entry.getKey());

        for (final Entry<String, Collection<ServerStorageStructure>> change : changesPerStructure.asMap().entrySet())
        {
            final String tableName = change.getValue().iterator().next().getOwner().getTableName();

            final String keyPart = SQLMaker.createKeyPart(vars, changeHolder, change.getValue().toArray(new ServerStorageStructure[change.getValue().size()]));

            builder.append(SQLMaker.createUpdateQuery(tableName, change.getKey(), keyPart)).append("\n");
        }
    }
}
