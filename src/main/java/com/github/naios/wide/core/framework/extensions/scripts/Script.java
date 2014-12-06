package com.github.naios.wide.core.framework.extensions.scripts;

import com.github.naios.wide.scripts.ScriptDefinition;

// TODO as scripts and modules are very similar inherit it from a base class
public abstract class Script
{
    private final ScriptDefinition definition;

    public Script(final ScriptDefinition definition)
    {
        this.definition = definition;
    }

    @Override
    public String toString()
    {
        return definition.getUUID();
    }

    public abstract void run(String[] args);

    public abstract String getUsage();
}
