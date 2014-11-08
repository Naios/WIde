package wide.core.config;

import wide.core.database.DatabaseTypes;

public class WIdeConfig extends Config
{
    private static final String[][] DEFAULT_PROPERTIES = new String[][]
    {
            // Default properties
            {"DB:User", "root"},
            {"DB:Host", "localhost"},
            {"DB:Port", "3306"},
            {"DB:Password", ""},
            {DatabaseTypes.DATABASE_AUTH.getStorageName(), "auth"},
            {DatabaseTypes.DATABASE_CHARACTER.getStorageName(), "character"},
            {DatabaseTypes.DATABASE_WORLD.getStorageName(), "world"},
            {"DB:Autologin", "false"}
    };

    protected String[][] getDefaultProperties()
    {
        return DEFAULT_PROPERTIES;
    }
}
