package wide.modules.console;

import wide.core.WIde;
import wide.core.framework.extensions.Module;
import wide.core.framework.ui.UserInferface;

public class Console extends Module implements UserInferface
{
    public Console()
    {
        super("default_console");
    }

    @Override
    public boolean check()
    {
        return !WIde.getArgs().isGuiApplication();
    }

    @Override
    public void enable()
    {
    }

    @Override
    public void disable()
    {
    }

    @Override
    public void show()
    {
        
    }
}
