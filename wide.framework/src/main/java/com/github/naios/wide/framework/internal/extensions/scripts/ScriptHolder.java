
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.extensions.scripts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.naios.wide.framework.WIde;
import com.github.naios.wide.framework.internal.extensions.Holder;
import com.github.naios.wide.framework.session.hooks.Hook;
import com.github.naios.wide.scripts.ScriptDefinition;

public class ScriptHolder extends Holder
{
    private final Map<String, Script> scripts = new HashMap<>();

    @Override
    protected void load()
    {
        for (final ScriptDefinition definition : ScriptDefinition.values())
        {
            /*
             * TODO do we need this?
             *if (script.validate())
             */

            final Script script;
            try
            {
                script = definition.newInstance();
            } catch (final Exception e)
            {
                continue;
            }

            scripts.put(script.toString(), script);

            if (WIde.getEnviroment().isTraceEnabled())
                System.out.println("Script " + script + " loaded.");
        }

        // Hook.ON_SCRIPTS_LOADED
        WIde.getHooks().fire(Hook.ON_SCRIPTS_LOADED);
    }

    @Override
    protected void unload()
    {
        scripts.clear();

        // Hook.ON_SCRIPTS_UNLOADED
        WIde.getHooks().fire(Hook.ON_SCRIPTS_UNLOADED);
    }

    public Set<String> getScriptNames()
    {
        return scripts.keySet();
    }

    public Script getScriptByName(final String name)
    {
        return scripts.get(name);
    }

    public boolean execute(final String cmd)
    {
        // Split space but no quotes
        final String[] args = cmd.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)");

        if (args.length < 1)
            return false;

        final Script script = getScriptByName(args[0]);
        if (script == null)
            return false;

        script.run(Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}
