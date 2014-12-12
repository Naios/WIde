
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.printflag;

import java.util.List;

import com.github.naios.wide.core.framework.extensions.scripts.Script;
import com.github.naios.wide.core.framework.storage.server.AliasUtil;
import com.github.naios.wide.core.framework.util.FlagUtil;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.scripts.ScriptDefinition;

public class PrintFlags extends Script
{
    public PrintFlags(final ScriptDefinition definition)
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

        final List<? extends Enum> flags = FlagUtil.getFlagList(enumeration, value);

        System.out.println(String.format("Value: %s = %s", value, StringUtil.asHex(value)));

        for (final Enum flag : flags)
            System.out.println(String.format("\n%-10s = %s", StringUtil.asHex(FlagUtil.createFlag(flag)), flag.name()));
    }

    @Override
    public String getUsage()
    {
        return "<enum> <flag>";
    }
}
