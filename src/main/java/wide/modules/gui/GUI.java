package wide.modules.gui;

import wide.core.WIde;
import wide.core.framework.extensions.modules.Module;
import wide.core.framework.ui.UserInferface;
import wide.modules.ModuleDefinition;
import wide.modules.gui.core.FXApplication;

public class GUI extends Module implements UserInferface
{
    public GUI(ModuleDefinition definition)
    {
        super(definition);
    }

    @Override
    public boolean validate()
    {
        return WIde.getEnviroment().isGuiApplication();
    }

    @Override
    public void onEnable()
    {
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public void show()
    {
        FXApplication.run(new String[]{});
    }
}
