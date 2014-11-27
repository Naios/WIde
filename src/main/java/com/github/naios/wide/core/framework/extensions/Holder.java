package com.github.naios.wide.core.framework.extensions;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public abstract class Holder
{
    public Holder()
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
