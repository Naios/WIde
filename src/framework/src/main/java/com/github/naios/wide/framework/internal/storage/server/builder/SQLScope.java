
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
import java.util.Objects;

import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.framework.storage.server.SQLScopeProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureChangeTracker;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class SQLScope
{
    private final Map<ServerStorage<?>, Multimap<ServerStorageStructure, SQLUpdateInfo>> update = new HashMap<>();

    private final Multimap<ServerStorage<?>, ServerStorageStructure> insert = HashMultimap.create(), delete = HashMultimap.create();

    // TODO make use of this
    private final QueryTypeConfig updateConfig, insertConfig, deleteConfig;

    protected SQLScope()
    {
        final QueryConfig config = FrameworkServiceImpl.getConfigService().getQueryConfig();

        // FIXME This is garbage
        updateConfig = config.getConfigForType(QueryType.UPDATE);
        insertConfig = config.getConfigForType(QueryType.INSERT);
        deleteConfig = config.getConfigForType(QueryType.DELETE);
    }

    Map<ServerStorage<?>, Multimap<ServerStorageStructure, SQLUpdateInfo>> getUpdate()
    {
        return update;
    }

    Multimap<ServerStorage<?>, ServerStorageStructure> getInsert()
    {
        return insert;
    }

    Multimap<ServerStorage<?>, ServerStorageStructure> getDelete()
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
    protected static Map<String, SQLScope> split(final SQLScopeProvider scopeProvider,
            final Map<ServerStorageStructure, Collection<SQLUpdateInfo>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        final Map<String, SQLScope> scopes = new HashMap<>();

        update.forEach((structure, infos) ->
        {
            infos.forEach(new SQLScopeSplitter<SQLUpdateInfo>(scopes)
            {
                @Override
                public String getScope(final SQLUpdateInfo info)
                {
                    return scopeProvider.getScopeOfEntry(structure, info.getEntry());
                }

                @Override
                public void addObservable(final SQLScope scope, final SQLUpdateInfo info)
                {
                    Multimap<ServerStorageStructure, SQLUpdateInfo> map = scope.update.get(structure.getOwner());
                    if (Objects.isNull(map))
                    {
                        map = HashMultimap.create();
                        scope.update.put(structure.getOwner(), map);
                    }

                    map.put(structure, info);
                }
            });
        });

        insert.forEach(new SQLScopeSplitter<ServerStorageStructure>(scopes)
        {
            @Override
            public String getScope(final ServerStorageStructure entry)
            {
                return scopeProvider.getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope, final ServerStorageStructure entry)
            {
                scope.insert.put(entry.getOwner(), entry);
            }
        });

        delete.forEach(new SQLScopeSplitter<ServerStorageStructure>(scopes)
        {
            @Override
            public String getScope(final ServerStorageStructure entry)
            {
                return scopeProvider.getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope, final ServerStorageStructure entry)
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
        for (final Entry<ServerStorage<?>, Multimap<ServerStorageStructure, SQLUpdateInfo>> structure : update.entrySet())
            buildUpdates(builder, changeTracker, structure.getValue(), vars);

        return builder.toString();
    }

    private void buildUpdates(
            final StringBuilder builder,
            final StructureChangeTracker changeTracker,
            final Multimap<ServerStorageStructure, SQLUpdateInfo> multimap,
            final SQLVariableHolder vars)
    {
        // TODO Group updates by key or updates
        final Multimap<String /*changes*/, ServerStorageStructure> changesPerStructure = HashMultimap.create();

        // Build Changeset (updates without key)
        for (final Entry<ServerStorageStructure, Collection<SQLUpdateInfo>> info : multimap.asMap().entrySet())
            changesPerStructure.put(SQLMaker.createUpdateFields(vars, changeTracker, info.getKey(), info.getValue()), info.getKey());

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
