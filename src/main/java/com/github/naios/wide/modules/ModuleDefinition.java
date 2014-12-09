
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.modules;

import com.github.naios.wide.core.framework.extensions.modules.Module;
import com.github.naios.wide.modules.terminal.Terminal;

public enum ModuleDefinition
{
    // Define new Modules here
    MODULE_TERMINAL("terminal", Terminal.class);

    private final String uuid;

    private final Class<? extends Module> type;

    private ModuleDefinition(final String uuid, final Class<? extends Module> type)
    {
        this.uuid = uuid;
        this.type = type;
    }

    public String getUUID()
    {
        return uuid;
    }

    public Module newInstance() throws Exception
    {
        return type.getDeclaredConstructor(ModuleDefinition.class).newInstance(this);
    }
}
