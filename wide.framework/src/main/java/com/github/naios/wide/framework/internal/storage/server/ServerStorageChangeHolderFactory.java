
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.util.HashMap;
import java.util.Map;

public class ServerStorageChangeHolderFactory
{
    private static final Map<String, ServerStorageChangeHolder> INSTANCES =
            new HashMap<>();

    public static ServerStorageChangeHolder instance(final String databaseId)
    {
        ServerStorageChangeHolder instance = INSTANCES.get(databaseId);
        if (instance == null)
        {
            instance = new ServerStorageChangeHolder(databaseId);
            INSTANCES.put(databaseId, instance);
        }
        return instance;
    }
}
