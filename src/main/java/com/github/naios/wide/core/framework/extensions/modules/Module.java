package com.github.naios.wide.core.framework.extensions.modules;

import java.util.LinkedList;
import java.util.List;

import com.github.naios.wide.modules.ModuleDefinition;

public abstract class Module
{
    private final ModuleDefinition definition;

    private final List<ModuleDefinition> requires = new LinkedList<ModuleDefinition>();

    public Module(ModuleDefinition definition)
    {
        this.definition = definition;
    }

    public abstract boolean validate();

    public abstract void onEnable();

    public abstract void onDisable();

    @Override
    public String toString()
    {
        return definition.getUUID();
    }

    public Module requires(ModuleDefinition definition)
    {
        requires.add(definition);
        return this;
    }
}
