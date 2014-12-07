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
