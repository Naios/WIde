package wide.modules.gui;

import wide.core.WIde;
import wide.core.framework.extensions.Extension;
import wide.core.framework.extensions.modules.Module;
import wide.core.framework.ui.UserInferface;

public class GraphicalInterface extends Module implements UserInferface
{
    public GraphicalInterface()
    {
        super("default_gui");
    }

    @Override
    public boolean validate()
    {
        return WIde.getArgs().isGuiApplication();
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
        System.err.println("Sorry, the GUI is currently not supported!\n"+
                           "Use the Console mode instead (WIde --nogui)");
    }
}
