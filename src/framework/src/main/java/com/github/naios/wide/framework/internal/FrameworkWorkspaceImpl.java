
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.naios.wide.api.config.main.EnvironmentConfig;
import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.SQLBuilder;
import com.github.naios.wide.api.framework.storage.server.SQLInfoProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.framework.internal.alias.AliasStorage;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.server.ChangeTrackerImpl;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageImpl;
import com.github.naios.wide.framework.internal.storage.server.builder.SQLBuilderImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class FrameworkWorkspaceImpl implements FrameworkWorkspace
{
    private final EnvironmentConfig config;

    private final AliasStorage aliases = new AliasStorage(this);

    private final Cache<String, ClientStorage<? extends ClientStorageStructure>> clientStorages =
            CacheBuilder
                .newBuilder()
                .build();

    private final Cache<String, ChangeTrackerImpl> changeTracker =
            CacheBuilder
                .newBuilder()
                .build();

    private final Cache<Pair<String, String>, ServerStorage<? extends ServerStorageStructure>> serverStorages =
            CacheBuilder
                .newBuilder()
                .build();

    public FrameworkWorkspaceImpl(final EnvironmentConfig config)
    {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(final String name)
    {
        try
        {
            return (ClientStorage<T>) clientStorages.get(name, () -> ClientStorageSelector.<T>select(name, ClientStoragePolicy.DEFAULT_POLICY));
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private ChangeTrackerImpl requestChangeTrackerImpl(final String databaseId)
    {
        try
        {
            return changeTracker.get(databaseId, () -> new ChangeTrackerImpl(this));
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChangeTracker requestChangeTracker(final String databaseId)
    {
        return requestChangeTrackerImpl(databaseId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(final String databaseId, final String name)
    {
        try
        {
            return (ServerStorage<T>) serverStorages.get(new Pair<>(databaseId, name), () -> new ServerStorageImpl<T>(databaseId, name, requestChangeTrackerImpl(databaseId)));
        }
        catch (final ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String requestAlias(final String name, final int value)
    {
        return aliases.requestAlias(name, value);
    }

    @Override
    public Map<Integer, String> requestAllAliases(final String name)
    {
        return aliases.requestAllAliases(name);
    }

    @Override
    public void reloadAliases()
    {
        aliases.reloadAliases();
    }

    @Override
    public SQLBuilder createSQLBuilder(final ChangeTracker changeTracker)
    {
        return createSQLBuilder(changeTracker,
                changeTracker.entriesChangedAsCollection(),
                changeTracker.structuresCreated(),
                changeTracker.structuresDeleted());
    }

    @Override
    public SQLBuilder createSQLBuilder(final ChangeTracker changeTracker,
                final QueryTypeConfig updateConfig,
                    final QueryTypeConfig insertConfig,
                        final QueryTypeConfig deleteConfig)
    {
        return createSQLBuilder(changeTracker,
                changeTracker.entriesChangedAsCollection(),
                changeTracker.structuresCreated(),
                changeTracker.structuresDeleted(),
                updateConfig,
                insertConfig,
                deleteConfig);
    }

    @Override
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Collection<SQLUpdateInfo> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete)
    {
        final QueryConfig queryConfig = FrameworkServiceImpl.getConfigService().getQueryConfig();
        return createSQLBuilder(sqlInfoProvider, update, insert, delete,
                queryConfig.getConfigForType(QueryType.UPDATE),
                queryConfig.getConfigForType(QueryType.INSERT),
                queryConfig.getConfigForType(QueryType.DELETE));
    }

    @Override
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Collection<SQLUpdateInfo> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete,
            final QueryTypeConfig updateConfig,
            final QueryTypeConfig insertConfig,
            final QueryTypeConfig deleteConfig)
    {
        return new SQLBuilderImpl(this, sqlInfoProvider, update, insert, delete, updateConfig, insertConfig, deleteConfig);
    }
}
