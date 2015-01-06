
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework;

import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public interface FrameworkService
{
    public <T extends ClientStorageStructure> ClientStorage<T> requestClientStorage(String name);

    public <T extends ServerStorageStructure> ServerStorage<T> requestServerStorage(String databaseId, String name);
}
