package wide.core.framework.extensions;

import wide.core.WIde;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;

public abstract class ExtensionHolder
{
    public ExtensionHolder()
    {
        WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_LOADED, this)
        {
            @Override
            public void informed()
            {
                load();
            }
        });

        WIde.getHooks().addListener(new HookListener(Hook.ON_APPLICATION_STOP, this)
        {
            @Override
            public void informed()
            {
                unload();
            }
        });
    }

    protected abstract void load();

    protected abstract void unload();
}
