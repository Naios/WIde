
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework;

import com.github.naios.wide.framework.storage.client.ClientStorage;
import com.github.naios.wide.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.framework.storage.server.ServerStorage;
import com.github.naios.wide.framework.storage.server.ServerStorageStructure;

public interface FrameworkService
{
    public <T extends ClientStorageStructure> ClientStorage<T> createClientStorage(String name);

    public <T extends ServerStorageStructure> ServerStorage<T> createServersStorage(String databaseId, String name);
}
