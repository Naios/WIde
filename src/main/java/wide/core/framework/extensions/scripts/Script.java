package wide.core.framework.extensions.scripts;

public abstract class Script
{
    // TODO do we need uuid since scripts are no modules?
    private final String uuid;

    public Script(String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public String toString()
    {
        return uuid;
    }

    public abstract void run(String[] args);

    public abstract String getUsage();
}
