package wide.core.session.database;

public enum ConfigEntry
{
    CONFIG_DATABASE_AUTH("DB:Auth"),
    CONFIG_DATABASE_CHARACTER("DB:Character"),
    CONFIG_DATABASE_WORLD("DB:World");

    private final String storageName;

    ConfigEntry(String storageName)
    {
        this.storageName = storageName;
    }

    public String getStorageName()
    {
        return storageName;
    }
}
