
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureChangeTracker;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class SQLScope
{
    private final Map<ServerStorage<?>, Multimap<ServerStorageStructure, Pair<ObservableValue<?>, MappingMetaData>>> update = new HashMap<>();

    private final Multimap<ServerStorage<?>, ServerStorageStructure> insert = HashMultimap.create(), delete = HashMultimap.create();

    private final QueryTypeConfig updateConfig, insertConfig, deleteConfig;

    protected SQLScope()
    {
        final QueryConfig config = FrameworkServiceImpl.getConfigService().getQueryConfig();

        updateConfig = config.getConfigForType(QueryType.UPDATE);
        insertConfig = config.getConfigForType(QueryType.INSERT);
        deleteConfig = config.getConfigForType(QueryType.DELETE);
    }

    protected Map<ServerStorage<?>, Multimap<ServerStorageStructure, Pair<ObservableValue<?>, MappingMetaData>>> getUpdate()
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

    protected boolean isEmpty()
    {
        return update.isEmpty() && insert.isEmpty() && delete.isEmpty();
    }

    /**
     * Splits collections containing update, insert & delete structures into its scopes
     */
    protected static Map<String, SQLScope> split(final StructureChangeTracker changeTracker,
            final Collection<Pair<ObservableValue<?>, MappingMetaData>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        final Map<String, SQLScope> scopes = new HashMap<>();

        update.forEach(new SQLScopeSplitter<Pair<ObservableValue<?>, MappingMetaData>>(changeTracker, scopes)
        {
            @Override
            public String getScope(
                    final Pair<ObservableValue<?>, MappingMetaData> entry)
            {
                return changeTracker.getScopeOfObservable(entry.first());
            }

            @Override
            public void addObservable(final SQLScope scope,
                    final Pair<ObservableValue<?>, MappingMetaData> entry)
            {
                scope.update.put(entry.second().getStructure().getOwner(), entry);
            }
        });

        insert.forEach(new SQLScopeSplitter<ServerStorageStructure>(changeTracker, scopes)
        {
            @Override
            public String getScope(
                    final ServerStorageStructure entry)
            {
                return changeTracker.getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope,
                    final ServerStorageStructure entry)
            {
                scope.insert.put(entry.getOwner(), entry);
            }
        });

        delete.forEach(new SQLScopeSplitter<ServerStorageStructure>(changeTracker, scopes)
        {
            @Override
            public String getScope(
                    final ServerStorageStructure entry)
            {
                return changeTracker.getScopeOfStructure(entry);
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

    protected String buildQuery(final String key, final SQLVariableHolder vars,  final StructureChangeTracker changeTracker)
    {
        final StringBuilder builder = new StringBuilder();

        // Build delete querys for each structure
        for (final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structure : delete.asMap().entrySet())
            buildDeletes(builder, changeTracker, structure, vars);

        // Build insert querys for each structure
        for (final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structure : insert.asMap().entrySet())
            buildInserts(builder, changeTracker, structure, vars);

        // Build upate querys for each structure
        for (final Entry<ServerStorage<?>, Multimap<ServerStorageStructure, Pair<ObservableValue<?>, MappingMetaData>>> structure : update.entrySet())
            buildUpdates(builder, changeTracker, structure.getValue(), vars);

        return builder.toString();
    }

    private void buildUpdates(
            final StringBuilder builder,
            final StructureChangeTracker changeTracker,
            final Multimap<ServerStorageStructure, Pair<ObservableValue<?>, MappingMetaData>> values,
            final SQLVariableHolder vars)
    {
        // TODO Group updates by key or updates
        final Multimap<String /*changes*/, ServerStorageStructure> changesPerStructure = HashMultimap.create();

        // Build Changeset (updates without key)
        for (final Entry<ServerStorageStructure, Collection<Pair<ObservableValue<?>, MappingMetaData>>> entry : values.asMap().entrySet())
            changesPerStructure.put(SQLMaker.createUpdateFields(vars, changeTracker, entry.getKey(), entry.getValue()), entry.getKey());

        for (final Entry<String, Collection<ServerStorageStructure>> change : changesPerStructure.asMap().entrySet())
        {
            final String tableName = Iterables.get(change.getValue(), 0).getOwner().getTableName();

            final String keyPart = SQLMaker.createKeyPart(vars, changeTracker, change.getValue());

            builder.append(SQLMaker.createUpdateQuery(tableName, change.getKey(), keyPart)).append("\n");
        }
    }

    private void buildDeletes(
            final StringBuilder builder,
            final StructureChangeTracker changeTracker,
            final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structures,
            final SQLVariableHolder vars)
    {
        final String tableName = Iterables.get(structures.getValue(), 0).getOwner().getTableName();

        final String keyPart = SQLMaker.createKeyPart(vars, changeTracker,structures.getValue());
        builder.append(SQLMaker.createDeleteQuery(tableName, keyPart)).append("\n");
    }

    private void buildInserts(
            final StringBuilder builder,
            final StructureChangeTracker changeTracker,
            final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structures,
            final SQLVariableHolder vars)
    {
        // Build delete before insert querys
        buildDeletes(builder, changeTracker, structures, vars);

        final ServerStorageStructure anyStructure = Iterables.get(structures.getValue(), 0);
        final String valuePart = SQLMaker.createInsertValuePart(vars, changeTracker, structures.getValue());

        builder.append(SQLMaker.createInsertQuery(anyStructure.getOwner().getTableName(),
                anyStructure.getValues(), valuePart)).append("\n");
    }
}
