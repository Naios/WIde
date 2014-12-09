
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.extensions.modules.type.implementation;

import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.framework.extensions.modules.type.VersionModule;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public abstract class VersionModuleImplementation implements VersionModule
{
    private final Map<String, Class<? extends ServerStorageStructure>> serverStructures =
            new HashMap<>();

    private final Map<String, Class<? extends ClientStorageStructure>> clientStructures =
            new HashMap<>();

    private VersionModuleImplementation addServer(final Class<? extends ServerStorageStructure> structure)
    {
        serverStructures.put(ServerStorageStructure.getStorageName(structure), structure);
        return this;
    }

    private VersionModuleImplementation addClient(final Class<? extends ClientStorageStructure> structure)
    {
        clientStructures.put(ClientStorageStructure.getStorageName(structure), structure);
        return this;
    }

    @Override
    public Class<? extends ServerStorageStructure> getServerStructure(
            final String name)
    {
        return serverStructures.get(name);
    }

    @Override
    public Class<? extends ClientStorageStructure> getClientStructure(
            final String name)
    {
        return clientStructures.get(name);
    }
}
