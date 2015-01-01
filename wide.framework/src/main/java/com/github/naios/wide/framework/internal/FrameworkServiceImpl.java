
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.ClientStorageConfig;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.framework.FrameworkService;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageImpl;

public final class FrameworkServiceImpl implements FrameworkService
{
    private static ConfigService config;

    private static DatabasePoolService database;

    public void start()
    {
        // Debug Code
        final ClientStorageConfig csc = config.getActiveEnviroment().getClientStorageConfig();

        System.out.println(String.format("DEBUG: %s", csc));
        System.out.println(String.format("DEBUG: %s = %s", csc.schemaPath().get(), csc.schema().get()));
        // ////

        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::start()"));

        final ServerStorage<CreatureTemplate> ct = new ServerStorageImpl<>("world", "creature_template");
        System.out.println(String.format("DEBUG: %s", ct.get(new ServerStorageKey<>(1))));
    }

    public void stop()
    {
        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::stop()"));
    }

    public void setConfig(final ConfigService config)
    {
        FrameworkServiceImpl.config = config;
    }

    public void setDatabase(final DatabasePoolService database)
    {
        FrameworkServiceImpl.database = database;
    }

    public static ConfigService getConfig()
    {
        return config;
    }

    public static DatabasePoolService getDatabase()
    {
        return database;
    }

    @Override
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(
            final String name)
    {
        // TODO @FrameworkIntegration
        return null;
    }

    @Override
    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(
            final String databaseId, final String name)
    {
        // TODO @FrameworkIntegration
        return null;
    }
}
