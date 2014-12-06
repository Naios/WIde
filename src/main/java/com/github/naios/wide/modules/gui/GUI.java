package com.github.naios.wide.modules.gui;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.extensions.modules.Module;
import com.github.naios.wide.core.framework.extensions.modules.type.UIModule;
import com.github.naios.wide.modules.ModuleDefinition;
import com.github.naios.wide.modules.gui.core.FXApplication;

public class GUI extends Module implements UIModule
{
    public GUI(final ModuleDefinition definition)
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
