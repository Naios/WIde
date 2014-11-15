package wide.core.framework.extensions.modules;

import wide.core.framework.extensions.Extension;

public abstract class Module extends Extension
{
    public Module(String uuid)
    {
        super(uuid);
    }

    public abstract void enable();

    public abstract void disable();
}
