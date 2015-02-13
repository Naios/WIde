
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

import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public final class SQLScope
{
    private final Map<ServerStorage<?>, Multimap<ServerStorageStructure, SQLUpdateInfo>> update = new HashMap<>();

    private final Multimap<ServerStorage<?>, ServerStorageStructure> insert = HashMultimap.create(), delete = HashMultimap.create();

    private final SQLBuilderImpl sqlBuilder;

    protected SQLScope(final SQLBuilderImpl builder)
    {
        this.sqlBuilder = builder;
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
    protected static Map<String, SQLScope> split(final SQLBuilderImpl sqlBuilder,
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
                public SQLBuilderImpl getSQLBuilder()
                {
                    return sqlBuilder;
                }

                @Override
                public String getScope(final SQLUpdateInfo info)
                {
                    return sqlBuilder.getSQLInfoProvider().getScopeOfEntry(structure, info.getProperty());
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
            public SQLBuilderImpl getSQLBuilder()
            {
                return sqlBuilder;
            }

            @Override
            public String getScope(final ServerStorageStructure entry)
            {
                return sqlBuilder.getSQLInfoProvider().getScopeOfStructure(entry);
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
            public SQLBuilderImpl getSQLBuilder()
            {
                return sqlBuilder;
            }

            @Override
            public String getScope(final ServerStorageStructure entry)
            {
                return sqlBuilder.getSQLInfoProvider().getScopeOfStructure(entry);
            }

            @Override
            public void addObservable(final SQLScope scope, final ServerStorageStructure entry)
            {
                scope.delete.put(entry.getOwner(), entry);
            }
        });

        return scopes;
    }

    protected String buildQuery()
    {
        final StringBuilder builder = new StringBuilder();

        // Build delete queries for each structure
        for (final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structure : delete.asMap().entrySet())
            buildDeletes(builder, structure);

        // Build insert queries for each structure
        for (final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structure : insert.asMap().entrySet())
            buildInserts(builder, structure);

        // Build upate queries for each structure
        for (final Entry<ServerStorage<?>, Multimap<ServerStorageStructure, SQLUpdateInfo>> structure : update.entrySet())
            buildUpdates(builder,  structure.getValue());

        return builder.toString();
    }

    private void buildUpdates(final StringBuilder builder,
            final Multimap<ServerStorageStructure, SQLUpdateInfo> multimap)
    {
        final SQLMaker sqlMaker = new SQLMaker(sqlBuilder, sqlBuilder.getUpdateConfig());

        // TODO Group updates by key or updates
        final Multimap<String /*changes*/, ServerStorageStructure> changesPerStructure = HashMultimap.create();

        // Build Changeset (updates without key)
        for (final Entry<ServerStorageStructure, Collection<SQLUpdateInfo>> info : multimap.asMap().entrySet())
            changesPerStructure.put(sqlMaker.createUpdateFields(info.getKey(), info.getValue()), info.getKey());

        for (final Entry<String, Collection<ServerStorageStructure>> change : changesPerStructure.asMap().entrySet())
        {
            final String tableName = Iterables.get(change.getValue(), 0).getOwner().getTableName();

            final String keyPart = sqlMaker.createKeyPart(change.getValue());

            builder.append(SQLMaker.createUpdateQuery(tableName, change.getKey(), keyPart));
        }

        if (!multimap.isEmpty())
            builder.append(SQLMaker.NEWLINE);
    }

    private void buildDeletes(final StringBuilder builder,
            final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structures)
    {
        final SQLMaker sqlMaker = new SQLMaker(sqlBuilder, sqlBuilder.getDeleteConfig());

        final String tableName = Iterables.get(structures.getValue(), 0).getOwner().getTableName();

        final String keyPart = sqlMaker.createKeyPart(structures.getValue());
        builder.append(SQLMaker.createDeleteQuery(tableName, keyPart)).append(SQLMaker.NEWLINE);
    }

    private void buildInserts(final StringBuilder builder,
            final Entry<ServerStorage<?>, Collection<ServerStorageStructure>> structures)
    {
        // Build delete before insert queries
        buildDeletes(builder, structures);

        final SQLMaker sqlMaker = new SQLMaker(sqlBuilder, sqlBuilder.getInsertConfig());

        if (!structures.getValue().isEmpty())
            builder.setLength(builder.length() - "\n".length());

        final ServerStorageStructure anyStructure = Iterables.get(structures.getValue(), 0);
        final String valuePart = sqlMaker.createInsertValuePart(structures.getValue());

        builder.append(sqlMaker.createInsertQuery(anyStructure.getOwner().getTableName(),
                anyStructure.getValues(), valuePart)).append(SQLMaker.NEWLINE);
    }
}
