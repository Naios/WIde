
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

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
