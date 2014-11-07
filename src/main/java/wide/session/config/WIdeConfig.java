package wide.session.config;


public class WIdeConfig extends Config
{
    private static final String[][] DEFAULT_PROPERTIES = new String[][]
    {
            // Default properties
            {"DB:User", "root"},
            {"DB:Host", "localhost"},
            {"DB:Port", "3306"},
            {"DB:Password", ""},
            {"DB:World", "world"},
            {"DB:Characters", "characters"},
            {"DB:Autologin", "false"}
    };

    public WIdeConfig(String file)
    {
        super(file);
    }

    protected String[][] getDefaultProperties()
    {
        return DEFAULT_PROPERTIES;
    }
}
