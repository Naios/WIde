package wide.core.framework.extensions.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import wide.core.WIde;
import wide.core.framework.extensions.ExtensionHolder;
import wide.core.session.hooks.Hook;
import wide.modules.ModuleLoader;

public class ModuleHolder extends ExtensionHolder
{
    private final Collection<Module> activated = new ArrayList<>();

    private final ModuleLoader loader = new ModuleLoader();

    // Load all Modules
    @Override
    protected void load()
    {
        for (final Module module : loader.getExtensions())
        {
            if (module.validate())
            {
                activated.add(module);
                module.enable();

                if(WIde.getArgs().isTraceEnabled())
                    System.out.println("Module " + module + " loaded.");
            }
        }

        // Hook.ON_MODULES_LOADED
        WIde.getHooks().fire(Hook.ON_MODULES_LOADED);
    }

    // Reload all Modules
    public  void reload()
    {
        unload();
        load();

        // Hook.ON_MODULES_RELOADED
        WIde.getHooks().fire(Hook.ON_MODULES_RELOADED);
    }

    // Check all Modules
    @Override
    protected void unload()
    {
        for (final Module module : activated)
            module.disable();

        // Hook.ON_MODULES_UNLOADED
        WIde.getHooks().fire(Hook.ON_MODULES_UNLOADED);
    }

    public List<Module> getModulesWithCheck(ModuleCheck checker)
    {
        final List<Module> modules = new LinkedList<>();

        for (final Module module : activated)
            if (checker.check(module))
                modules.add(module);

        return modules;
    }
}
