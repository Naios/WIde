package wide.modules;

import wide.core.framework.extensions.Module;
import wide.modules.gui.GraphicalInterface;
import wide.modules.terminal.Terminal;

public class Loader
{
    // Module Loader, insert new Module instances here
    private static final Module[] MODULES =
    {
        new Terminal(),
        new GraphicalInterface()
    };

    public static Module[] getModules()
    {
        return MODULES;
    }
}
