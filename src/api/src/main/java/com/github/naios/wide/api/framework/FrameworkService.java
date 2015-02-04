
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework;

import java.util.Collection;
import java.util.Map;

import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.SQLBuilder;
import com.github.naios.wide.api.framework.storage.server.SQLInfoProvider;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public interface FrameworkService extends AliasFactory
{
    /**
     * TODO
     * @param name
     * @return
     */
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(String name);

    /**
     * TODO
     * @param databaseId
     * @param name
     * @return
     */
    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(String databaseId, String name);

    /**
     * TODO
     *
     * @param sqlInfoProvider
     * @param update
     * @param insert
     * @param delete
     * @return
     */
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Map<ServerStorageStructure, Collection<SQLUpdateInfo>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete);

    /**
     * TODO
     *
     * @param sqlInfoProvider
     * @param update
     * @param insert
     * @param delete
     * @param updateConfig
     * @param insertConfig
     * @param deleteConfig
     * @return
     */
    public SQLBuilder createSQLBuilder(final SQLInfoProvider sqlInfoProvider,
            final Map<ServerStorageStructure, Collection<SQLUpdateInfo>> update,
            final Collection<ServerStorageStructure> insert,
            final Collection<ServerStorageStructure> delete,
            final QueryTypeConfig updateConfig,
            final QueryTypeConfig insertConfig,
            final QueryTypeConfig deleteConfig);

    /**
     * TODO
     *
     * @param changeTracker
     * @return
     */
    public SQLBuilder createSQLBuilder(final ChangeTracker changeTracker);

    /**
     * TODO
     *
     * @param changeTracker
     * @param updateConfig
     * @param insertConfig
     * @param deleteConfig
     * @return
     */
    public SQLBuilder createSQLBuilder(ChangeTracker changeTracker,
            QueryTypeConfig updateConfig, QueryTypeConfig insertConfig,
            QueryTypeConfig deleteConfig);
}
