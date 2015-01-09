
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

public interface ServerStorageVersionMapController extends ServerStorageVersionMap
{
    public void addChange(final ServerStorage<?> storage, ServerStorageStructure structure, ServerStorageVersion version);

    public void removeChange(final ServerStorage<?> storage, ServerStorageStructure structure, ServerStorageVersion version);
}
