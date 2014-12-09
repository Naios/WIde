
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.extensions.modules;

import java.util.LinkedList;
import java.util.List;

import com.github.naios.wide.modules.ModuleDefinition;

public abstract class Module
{
    private final ModuleDefinition definition;

    private final List<ModuleDefinition> requires = new LinkedList<ModuleDefinition>();

    public Module(final ModuleDefinition definition)
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

    public Module requires(final ModuleDefinition definition)
    {
        requires.add(definition);
        return this;
    }
}
