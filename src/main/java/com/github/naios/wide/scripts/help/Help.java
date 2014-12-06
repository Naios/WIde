package com.github.naios.wide.scripts.help;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.scripts.ScriptDefinition;

public class Help extends Script
{
    public Help(final ScriptDefinition definition)
    {
        super(definition);
    }

    @Override
    public void run(final String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Console usage: {script name} {arguments}...");
            System.out.println();
            System.out.println("There are following scripts available,");
            System.out.println("use \"help " + getUsage() + "\" to get detailed info.");
            System.out.println();

            for (final String name : WIde.getScripts().getScriptNames())
                System.out.println(name);
        }
        else
        {
            final Script script = WIde.getScripts().getScriptByName(args[0]);
            if (script != null)
                System.out.println("Usage: " + script.toString() + " " + script.getUsage());
            else
                System.out.println("There is no script called \"" + args[0] + "\"");
        }
    }

    @Override
    public String getUsage()
    {
        return "{script name} arguments...";
    }
}
