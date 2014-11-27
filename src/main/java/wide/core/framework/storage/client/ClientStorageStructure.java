package wide.core.framework.storage.client;

public abstract class ClientStorageStructure
{
    private final String regex;

    protected ClientStorageStructure()
    {
        this(".*");
    }

    protected ClientStorageStructure(final String mask)
    {
        this.regex = mask;
    }

    public String getRegex()
    {
        return regex;
    }
}
