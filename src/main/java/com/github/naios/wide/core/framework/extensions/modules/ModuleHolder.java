package com.github.naios.wide.core.framework.extensions.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.extensions.Holder;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.modules.ModuleDefinition;

public class ModuleHolder extends Holder
{
    private final List<Module> modulesLoaded = new ArrayList<>();

    // Load all Modules
    @Override
    protected void load()
    {
        for (final ModuleDefinition definition : ModuleDefinition.values())
        {
            final Module module;
            try
            {
                module = definition.newInstance();
            }
            catch (final Exception e)
            {
                continue;
            }

            if (module.validate())
            {
                modulesLoaded.add(module);
                module.onEnable();

                if(WIde.getEnviroment().isTraceEnabled())
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
        for (final Module module : modulesLoaded)
            module.onDisable();

        // Hook.ON_MODULES_UNLOADED
        WIde.getHooks().fire(Hook.ON_MODULES_UNLOADED);
    }

    public List<Module> getModulesInstanceOf(Class<?> type)
    {
        final List<Module> modules = new LinkedList<>();

        for (final Module module : modulesLoaded)
            if (type.isAssignableFrom(module.getClass()))
                modules.add(module);

        return modules;
    }
}
