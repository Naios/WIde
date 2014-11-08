package wide.core.framework.extensions;

import java.util.ArrayList;
import java.util.Collection;

import wide.core.WIde;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;
import wide.modules.console.Console;
import wide.modules.gui.GraphicalInterface;

public class ModuleLoader
{
    // Module Loader, insert new Module instances here
    private static final Module[] MODULES =
    {
        new Console(),
        new GraphicalInterface()
    };

    private final Collection<Module> activated = new ArrayList<>();

    public ModuleLoader()
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

    // Load all Modules (Check dependencys)
    private void load()
    {
        for (Module module : MODULES)
        {
            module.read();

            if (module.checkDependencys() && module.check())
            {
                activated.add(module);
                module.enable();
            }
        }

        // Hook.ON_MODULES_LOADED
        WIde.getHooks().fire(Hook.ON_MODULES_LOADED);
    }

    // Reload all Modules
    private void reload()
    {
        unload();
        load();
        
        // Hook.ON_MODULES_RELOADED
        WIde.getHooks().fire(Hook.ON_MODULES_RELOADED);
    }

    // Check all Modules
    private void unload()
    {
     
        for (Module module : activated)
            module.disable();
        
        // Hook.ON_MODULES_UNLOADED
        WIde.getHooks().fire(Hook.ON_MODULES_UNLOADED);
    }
}
