package wide.core.session.database;

import wide.core.Constants;

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
