package wide.core.session.config;

import wide.core.session.database.ConfigEntry;

public class WIdeConfig extends Config
{
    private static final String[][] DEFAULT_PROPERTIES = new String[][]
    {
            // Default properties
            {"DB:User", "root"},
            {"DB:Host", "localhost"},
            {"DB:Port", "3306"},
            {"DB:Password", ""},
            {ConfigEntry.CONFIG_DATABASE_AUTH.getStorageName(), "auth"},
            {ConfigEntry.CONFIG_DATABASE_CHARACTER.getStorageName(), "character"},
            {ConfigEntry.CONFIG_DATABASE_WORLD.getStorageName(), "world"},
            {"DB:Autologin", "false"}
    };

    protected String[][] getDefaultProperties()
    {
        return DEFAULT_PROPERTIES;
    }
}
