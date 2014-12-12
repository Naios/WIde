
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.scripts.fetch.Fetch;
import com.github.naios.wide.scripts.hello.HelloWorld;
import com.github.naios.wide.scripts.help.Help;
import com.github.naios.wide.scripts.printdbc.PrintDBC;
import com.github.naios.wide.scripts.printenum.PrintEnum;
import com.github.naios.wide.scripts.printflag.PrintFlags;
import com.github.naios.wide.scripts.test.Test;

public enum ScriptDefinition
{
    // Define new Modules here
    SCRIPT_HELP("help", Help.class),
    SCRIPT_HELLO("hello", HelloWorld.class),
    SCRIPT_TEST("test", Test.class),
    SCRIPT_FETCH("fetch", Fetch.class),
    SCRIPT_PRINTDBC("printdbc", PrintDBC.class),
    SCRIPT_PRINTENUM("printenum", PrintEnum.class),
    SCRIPT_PRINTFLAGS("printflags", PrintFlags.class);

    private final String uuid;

    private final Class<? extends Script> type;

    private ScriptDefinition(final String uuid, final Class<? extends Script> type)
    {
        this.uuid = uuid;
        this.type = type;
    }

    public String getUUID()
    {
        return uuid;
    }

    public Script newInstance() throws Exception
    {
        return type.getDeclaredConstructor(ScriptDefinition.class).newInstance(this);
    }
}

