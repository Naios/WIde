
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
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
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public final class FrameworkServiceImpl implements FrameworkService
{
    private ConfigService config;

    private DatabasePoolService database;

    public void start()
    {
        // Debug Code
        final ClientStorageConfig csc = config.getActiveEnviroment().getClientStorageConfig();

        System.out.println(String.format("DEBUG: %s", csc));
        System.out.println(String.format("DEBUG: %s = %s", csc.schemaPath().get(), csc.schema().get()));
        // ////

        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::start()"));
    }

    public void stop()
    {
        System.out.println(String.format("DEBUG: %s", "FrameworkServiceImpl::stop()"));
    }

    public void setConfig(final ConfigService config)
    {
        this.config = config;
    }

    public void setDatabase(final DatabasePoolService database)
    {
        this.database = database;
    }

    @Override
    public <T extends ClientStorageStructure> ClientStorage<T> createClientStorage(
            final String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ServerStorageStructure> ServerStorage<T> createServersStorage(
            final String databaseId, final String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
