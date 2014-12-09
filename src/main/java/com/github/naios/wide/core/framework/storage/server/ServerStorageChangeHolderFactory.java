
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.session.database.DatabaseType;

public class ServerStorageChangeHolderFactory
{
    private static final Map<DatabaseType, ServerStorageChangeHolder> INSTANCES =
            new HashMap<>();

    public static ServerStorageChangeHolder instance(final DatabaseType databaseType)
    {
        ServerStorageChangeHolder instance = INSTANCES.get(databaseType);
        if (instance == null)
        {
            instance = new ServerStorageChangeHolder(databaseType);
            INSTANCES.put(databaseType, instance);
        }
        return instance;
    }
}
