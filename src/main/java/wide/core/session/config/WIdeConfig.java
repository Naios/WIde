package wide.core.session.config;

import wide.core.session.database.ConfigEntry;

public class WIdeConfig extends Config
{
    private static final String[][] DEFAULT_PROPERTIES = new String[][]
    {
            // Default properties
            {ConfigEntry.CONFIG_DATABASE_USER.getStorageName(), "root"},
            {ConfigEntry.CONFIG_DATABASE_HOST.getStorageName(), "localhost"},
            {ConfigEntry.CONFIG_DATABASE_PORT.getStorageName(), "3306"},
            {ConfigEntry.CONFIG_DATABASE_PASSWORD.getStorageName(), ""},
            {ConfigEntry.CONFIG_DATABASE_AUTH.getStorageName(), "auth"},
            {ConfigEntry.CONFIG_DATABASE_CHARACTER.getStorageName(), "character"},
            {ConfigEntry.CONFIG_DATABASE_WORLD.getStorageName(), "world"},
            {ConfigEntry.CONFIG_DATABASE_AUTOLOGIN.getStorageName(), "false"}
    };

    @Override
    protected String[][] getDefaultProperties()
    {
        return DEFAULT_PROPERTIES;
    }
}
