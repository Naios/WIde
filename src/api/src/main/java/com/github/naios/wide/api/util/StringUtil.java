
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

import java.util.Iterator;

public class StringUtil
{
    final private static String SPACE = " ";

    final private static String COMMA = ",";

    final private static String NEWLINE = "\n";

    public static String convertStringToVarName(final String str)
    {
        return str
                .toUpperCase()
                    .replaceAll("[:punct:\\(\\)\\]\\[']-", "")
                        .replaceAll(" +", "_");
    }

    public static String fillWithSpaces(final Object... array)
    {
        return concat(SPACE, array);
    }

    public static String fillWithComma(final Object... array)
    {
        return concat(COMMA, array);
    }

    public static String fillWithNewLines(final Object... array)
    {
        return concat(NEWLINE, array);
    }

    public static String concat(final String delemiter, final Object... array)
    {
        return concat(delemiter, new Iterator<String>()
        {
            int pos = 0;

            @Override
            public boolean hasNext()
            {
                return pos < array.length;
            }

            @Override
            public String next()
            {
                return array[pos++].toString();
            }
        });
    }

    public static String concat(final Iterable<String> iterable)
    {
        return concat(iterable.iterator());
    }

    public static String concat(final String delemiter, final Iterable<String> iterable)
    {
        return concat(delemiter, iterable.iterator());
    }

    public static String concat(final Iterator<String> iterator)
    {
        return concat(new String(), iterator);
    }

    public static String concat(final String delemiter, final Iterator<String> iterator)
    {
        final StringBuilder builder = new StringBuilder();

        while (iterator.hasNext())
            builder.append(iterator.next()).append(delemiter);

        if (builder.length() >= delemiter.length())
            builder.delete(builder.length() - delemiter.length(), builder.length());

        return builder.toString();
    }

    public static String asHex(final int value)
    {
        return "0x" + Integer.toHexString(value);
    }

    public static String asBin(final int value)
    {
        return "0b" + Integer.toBinaryString(value);
    }

    /**
     * Converts hex and bin into int if necessary
     */
    public static int convertToInt(final String value)
    {
        if (value.startsWith("0x"))
            return Integer.parseInt(value.substring(2), 16);
        else if (value.startsWith("0b"))
            return Integer.parseInt(value.substring(2), 1);
        else
            return Integer.valueOf(value);
    }
}
