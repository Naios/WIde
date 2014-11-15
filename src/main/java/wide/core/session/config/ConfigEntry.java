package wide.core.session.config;

public enum ConfigEntry
{
    CONFIG_DATABASE_USER("db.user"),
    CONFIG_DATABASE_HOST("db.host"),
    CONFIG_DATABASE_PORT("db.port"),
    CONFIG_DATABASE_PASSWORD("db.password"),

    CONFIG_DATABASE_AUTH("db.auth"),
    CONFIG_DATABASE_CHARACTER("db.character"),
    CONFIG_DATABASE_WORLD("db.world"),

    CONFIG_DATABASE_AUTOLOGIN("db.autologin"),
    CONFIG_DATABASE_SAVE_PASSWORD("db.savepassword");

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
