
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FormatterWrapper
{
    public enum Options
    {
        NO_STRING_ENCLOSURE,
        NO_FLOAT_DOUBLE_POSTFIX,
        NO_HEX_AND_BIN_ENCLOSE
    }

    final Object obj;

    final Set<Options> options = new HashSet<>();

    public FormatterWrapper(final Object obj, final Options... options)
    {
        this.obj = obj;

        this.options.addAll(Arrays.asList(options));
    }

    @Override
    public String toString()
    {
        if (obj == null)
            return Constants.STRING_NULL.toString();
        else if (obj instanceof Integer)
            return obj.toString();
        else if ((obj instanceof Float) && !hasOption(Options.NO_FLOAT_DOUBLE_POSTFIX))
            return obj.toString() + "f";
        else if ((obj instanceof Double) && !hasOption(Options.NO_FLOAT_DOUBLE_POSTFIX))
            return obj.toString() + "d";
        else if ((obj instanceof String) && !hasOption(Options.NO_STRING_ENCLOSURE))
        {
            if (hasOption(Options.NO_HEX_AND_BIN_ENCLOSE) && (obj.toString().startsWith("0x") || obj.toString().startsWith("0b")))
                return obj.toString();
            else
                return "\"" + obj.toString() + "\"";
        }
        else
            return obj.toString();
    }

    public boolean hasOption(final Options option)
    {
        return options.contains(option);
    }

    public static Object format(final Object obj, final boolean format, final Options... options)
    {
        if (format)
            return new FormatterWrapper(obj, options);
        else
            return obj;
    }
}
