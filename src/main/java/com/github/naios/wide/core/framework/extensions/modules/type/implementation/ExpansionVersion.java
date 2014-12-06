package com.github.naios.wide.core.framework.extensions.modules.type.implementation;

import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.framework.extensions.modules.type.VersionModule;
import com.github.naios.wide.core.framework.game.Expansion;
import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public class ExpansionVersion implements VersionModule
{
    private final GameBuildMask gamebuilds;

    private final Map<String, Class<? extends ServerStorageStructure>> serverStructures =
            new HashMap<>();

    private final Map<String, Class<? extends ClientStorageStructure>> clientStructures =
            new HashMap<>();

    public ExpansionVersion(final Expansion expansion)
    {
        gamebuilds = new GameBuildMask().addExpansion(expansion);
    }

    private ExpansionVersion addServer(final Class<? extends ServerStorageStructure> structure)
    {
        serverStructures.put(ServerStorageStructure.getStorageName(structure), structure);
        return this;
    }

    private ExpansionVersion addClient(final Class<? extends ClientStorageStructure> structure)
    {
        clientStructures.put(ClientStorageStructure.getStorageName(structure), structure);
        return this;
    }

    @Override
    public GameBuildMask getGameBuilds()
    {
        return gamebuilds;
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
