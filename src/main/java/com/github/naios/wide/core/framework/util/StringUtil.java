
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

public class StringUtil
{
    public static String convertStringToVarName(final String str)
    {
        // TODO Find a better regex for this
        return str
                .toUpperCase()
                .replace("[", "")
                .replace("]", "")
                .replace("-", "")
                .replace(" ", "_")
                .replace(")", "")
                .replace("(", "")
                .replace(":", "")
                .replace(".", "")
                .replaceAll("_{2}", "_")
                .replaceAll("[()'@\"]", "");
    }

    public static String concat(final Object[] array, final String delemiter)
    {
        final StringBuilder builder = new StringBuilder();

        for (final Object obj : array)
            builder.append(obj.toString()).append(delemiter);

        if (builder.length() >= delemiter.length())
            builder.delete(builder.length() - delemiter.length(), builder.length());

        return builder.toString();
    }
}
