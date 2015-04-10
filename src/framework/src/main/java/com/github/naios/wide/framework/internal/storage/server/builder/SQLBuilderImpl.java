
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.server.SQLBuilder;
import com.github.naios.wide.api.framework.storage.server.SQLInfoProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Implementation of an SQLBuilder based on storage holders
 */
public final class SQLBuilderImpl implements SQLBuilder
{
    private final FrameworkWorkspace workspace;

    private final SQLInfoProvider sqlInfoProvider;

    private final Multimap<ServerStorageStructure, SQLUpdateInfo> update;

    private final Collection<ServerStorageStructure> insert, delete;

    private final QueryTypeConfig updateConfig, insertConfig, deleteConfig;

    private final SQLVariableHolder variableHolder = new SQLVariableHolder();

    public SQLBuilderImpl(final FrameworkWorkspace workspace,
            final SQLInfoProvider sqlInfoProvider,
            final Collection<SQLUpdateInfo> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete,
            final QueryTypeConfig updateConfig,
            final QueryTypeConfig insertConfig,
            final QueryTypeConfig deleteConfig)
    {
        this.workspace = workspace;

        this.sqlInfoProvider = sqlInfoProvider;

        this.update = splitUpdateInfo(update);
        this.insert = insert;
        this.delete = delete;

        this.updateConfig = updateConfig;
        this.insertConfig = insertConfig;
        this.deleteConfig = deleteConfig;
    }

    private static Multimap<ServerStorageStructure, SQLUpdateInfo> splitUpdateInfo(final Collection<SQLUpdateInfo> updates)
    {
        final Multimap<ServerStorageStructure, SQLUpdateInfo> map = HashMultimap.create();
        updates.forEach(update -> map.put(MappingBeans.getStructure(update.getProperty()), update));
        return map;
    }

    public FrameworkWorkspace getWorkspace()
    {
        return workspace;
    }

    public SQLInfoProvider getSQLInfoProvider()
    {
        return sqlInfoProvider;
    }

    public SQLVariableHolder getVariableHolder()
    {
        return variableHolder;
    }

    public QueryTypeConfig getUpdateConfig()
    {
        return updateConfig;
    }

    public QueryTypeConfig getInsertConfig()
    {
        return insertConfig;
    }

    public QueryTypeConfig getDeleteConfig()
    {
        return deleteConfig;
    }

    /**
     * Builds our SQL query
     */
    @Override
    public synchronized void write(final OutputStream stream)
    {
        final PrintWriter writer = new PrintWriter(stream);
        variableHolder.clear();

        // Pre calculate everything
        final Map<String /*scope*/, SQLScope> scopes = SQLScope.split(this, update.asMap(), insert, delete);
        final Map<String /*scope*/, String /*query*/> queries = new HashMap<>();

        for (final Entry<String, SQLScope> entry : scopes.entrySet())
            queries.put(entry.getKey(), entry.getValue().buildQuery());

        variableHolder.writeQuery(writer);

        for (final Entry<String, String> entry : queries.entrySet())
        {
            final String comment = sqlInfoProvider.getCommentOfScope(entry.getKey());

            if (!comment.isEmpty())
                writer.println(SQLMaker.createComment(comment));

            // The actual scope queries
            writer.print(entry.getValue());
        }

        writer.close();
    }

    @Override
    public String toString()
    {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(stream);
        return new String(stream.toByteArray()).trim();
    }
}
