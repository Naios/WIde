package wide.core.framework.extensions;

public abstract class Extension
{
    private String uuid;

    public Extension(String uuid)
    {
        this.uuid = uuid;
    }

    public boolean validate()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return uuid;
    }
}
