package wide.core;

public enum Constants
{
    DEFAULT_PROPERTIES_NAME("WIde.properties"),
    DEFAULT_PROPERTIES_CREATE_PATH("properties/default.properties");

    private final String constant;

    Constants(String constant)
    {
        this.constant = constant;
    }

    public String get()
    {
        return constant;
    }
}
