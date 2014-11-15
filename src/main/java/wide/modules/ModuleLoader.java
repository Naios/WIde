package wide.modules;

import wide.core.framework.extensions.ExtensionLoader;
import wide.core.framework.extensions.modules.Module;
import wide.modules.gui.GUI;
import wide.modules.terminal.Terminal;

public class ModuleLoader implements ExtensionLoader<Module>
{
    // Module Loader, insert new Module instances here
    private final Module[] MODULES =
    {
        new Terminal(),
        new GUI()
    };

    @Override
    public Module[] getExtensions()
    {
        return MODULES;
    }
}
