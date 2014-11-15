package wide.core.session.database;

import wide.core.session.config.ConfigEntry;

public enum DatabaseType
{
    AUTH(ConfigEntry.CONFIG_DATABASE_AUTH),
    CHARACTER(ConfigEntry.CONFIG_DATABASE_CHARACTER),
    WORLD(ConfigEntry.CONFIG_DATABASE_WORLD);

    private final ConfigEntry entry;

    DatabaseType(ConfigEntry entry)
    {
        this.entry = entry;
    }

    public ConfigEntry getConfigEntry()
    {
        return entry;
    }
}
