package wide.core.framework.extensions.scripts;

import wide.core.framework.extensions.Extension;

public abstract class Script implements Extension
{
    // TODO do we need uuid since scripts are no modules?
    private final String uuid;

    public Script(String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public String getUUID()
    {
        return uuid;
    }

    public abstract void run(String[] args);

    public abstract String getUsage();
}
