package wide.core.framework.extensions.scripts;

import wide.core.framework.extensions.Extension;

public abstract class Script extends Extension
{
    public Script(String uuid)
    {
        super(uuid);
    }

    public abstract void run(String[] args);
}
