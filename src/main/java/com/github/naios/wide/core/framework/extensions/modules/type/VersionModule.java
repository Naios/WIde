
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.extensions.modules.type;

import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

public interface VersionModule
{
    public GameBuildMask getGameBuilds();

    public Class<? extends ServerStorageStructure> getServerStructure(final String name);

    public Class<? extends ClientStorageStructure> getClientStructure(final String name);
}
