package wide.modules.gui;

import wide.core.WIde;
import wide.core.framework.extensions.modules.Module;
import wide.core.framework.ui.UserInferface;
import wide.modules.gui.core.FXApplication;

public class GUI extends Module implements UserInferface
{
    public GUI()
    {
        super("gui");
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
        FXApplication.run(new String[]{});
    }
}
