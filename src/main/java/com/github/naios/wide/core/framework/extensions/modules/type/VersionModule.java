package com.github.naios.wide.core.framework.extensions.modules.type;

import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public interface VersionModule
{
    public GameBuildMask getVersions();

    public Class<? extends ServerStorageStructure> getServerStructure(final String name);

    public Class<? extends ClientStorageStructure> getClientStructure(final String name);
}
