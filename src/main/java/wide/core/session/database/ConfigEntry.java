package wide.core.session.database;

public enum ConfigEntry
{
    CONFIG_DATABASE_USER("DB:User"),
    CONFIG_DATABASE_HOST("DB:Host"),
    CONFIG_DATABASE_PORT("DB:Port"),
    CONFIG_DATABASE_PASSWORD("DB:Password"),

    CONFIG_DATABASE_AUTH("DB:Auth"),
    CONFIG_DATABASE_CHARACTER("DB:Character"),
    CONFIG_DATABASE_WORLD("DB:World"),

    CONFIG_DATABASE_AUTOLOGIN("DB:Autologin");

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
