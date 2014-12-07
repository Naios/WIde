package com.github.naios.wide.core.session.database;

import com.github.naios.wide.core.Constants;

public enum DatabaseType
{
    AUTH(Constants.PROPERTY_DATABASE_AUTH, "auth"),
    CHARACTER(Constants.PROPERTY_DATABASE_CHARACTER, "character"),
    WORLD(Constants.PROPERTY_DATABASE_WORLD, "world");

    private final Constants entry;

    private final String id;

    DatabaseType(final Constants entry, final String id)
    {
        this.entry = entry;
        this.id  = id;
    }

    public Constants getConfigEntry()
    {
        return entry;
    }

    public String getId()
    {
        return id;
    }

    public static DatabaseType getFromId(final String id)
    {
        for (final DatabaseType me : values())
            if (me.id.equals(id))
                return me;

        return null;
    }
}
