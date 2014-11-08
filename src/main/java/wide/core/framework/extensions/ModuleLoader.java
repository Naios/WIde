package wide.core.framework.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import wide.core.WIde;
import wide.core.framework.ui.UserInferface;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;
import wide.modules.Loader;
import wide.modules.gui.GraphicalInterface;
import wide.modules.terminal.Terminal;

public class ModuleLoader
{
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
        for (Module module : Loader.getModules())
        {
            module.read();

            boolean dep = module.checkDependencys(), check = module.check();
                        
            if (WIde.getArgs().isTraceEnabled())
                System.out.print("Module: " + module + "\tDependencys: " + dep + ", Checks: " + check + ", Loaded: >> ");
            
            if (dep && check)
            {
                if (WIde.getArgs().isTraceEnabled())
                    System.out.print("YES\n");
                
                activated.add(module);
                module.enable();
            }
            else if(WIde.getArgs().isTraceEnabled())
                System.out.print("NO\n");
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

    public List<UserInferface> getUserInterfaces()
    {
        final List<UserInferface> interfaces = new LinkedList<>();

        for (Module module : activated)
            if (module instanceof UserInferface)
                interfaces.add((UserInferface)(module));

        return interfaces;
    }
}
