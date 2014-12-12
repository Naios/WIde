
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.printenum;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.storage.server.AliasUtil;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.scripts.ScriptDefinition;

public class PrintEnum extends Script
{
    public PrintEnum(final ScriptDefinition definition)
    {
        super(definition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void run(final String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: " + getUsage());
            return;
        }

        final Class<? extends Enum> enumeration;
        try
        {
            enumeration = AliasUtil.getEnum(args[0]);
        }
        catch (final Exception e)
        {
            System.out.println(String.format("Enum %s was not found!", args[0]));
            return;
        }

        final int value = Integer.valueOf(args[1]);

        final Enum enumValue;
        try
        {
            enumValue = enumeration.getEnumConstants()[value];
        }
        catch (final Exception e)
        {
            System.out.println(String.format("Value %s is not a part of Enum %s.", value, args[0]));
            return;
        }

        System.out.println(String.format("Value: %s = %s = %s", value, StringUtil.asHex(value), enumValue.name()));
    }

    @Override
    public String getUsage()
    {
        return "<enum> <flag>";
    }
}
