package wide.core.session.config;

public enum ConfigEntry
{
    CONFIG_DATABASE_USER("core.session.database.user"),
    CONFIG_DATABASE_HOST("core.session.database.host"),
    CONFIG_DATABASE_PORT("core.session.database.port"),
    CONFIG_DATABASE_PASSWORD("core.session.database.password"),

    CONFIG_DATABASE_AUTH("core.session.database.auth"),
    CONFIG_DATABASE_CHARACTER("core.session.database.character"),
    CONFIG_DATABASE_WORLD("core.session.database.world"),

    CONFIG_DATABASE_AUTOLOGIN("core.session.database.autologin"),
    CONFIG_DATABASE_SAVE_PASSWORD("core.session.database.savepassword");

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
