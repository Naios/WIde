package com.github.naios.wide.scripts;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.scripts.fetch.Fetch;
import com.github.naios.wide.scripts.hello.HelloWorld;
import com.github.naios.wide.scripts.help.Help;
import com.github.naios.wide.scripts.printdbc.PrintDBC;
import com.github.naios.wide.scripts.test.Test;

public enum ScriptDefinition
{
    // Define new Modules here
    SCRIPT_HELP("help", Help.class),
    SCRIPT_HELLO("hello", HelloWorld.class),
    SCRIPT_TEST("test", Test.class),
    SCRIPT_FETCH("fetch", Fetch.class),
    SCRIPT_PRINTDBC("printdbc", PrintDBC.class);

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

