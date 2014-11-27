package com.github.naios.wide.core.session.database;

import com.github.naios.wide.core.Constants;

public enum DatabaseType
{
    AUTH(Constants.PROPERTY_DATABASE_AUTH),
    CHARACTER(Constants.PROPERTY_DATABASE_CHARACTER),
    WORLD(Constants.PROPERTY_DATABASE_WORLD);

    private final Constants entry;

    DatabaseType(Constants entry)
    {
        this.entry = entry;
    }

    public Constants getConfigEntry()
    {
        return entry;
    }
}
