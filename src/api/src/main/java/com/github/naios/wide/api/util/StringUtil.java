
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StringUtil
{
    final private static String SPACE = " ";

    final private static String COMMA = ",";

    final private static String NEWLINE = "\n";

    public static String convertStringToVarName(final String str)
    {
        return str
                .toUpperCase()
                    .replaceAll("[:punct:\\(\\)\\]\\['-]", "")
                        .replaceAll(" +", "_");
    }

    public static String fillWithSpaces(final Object... array)
    {
        return join(SPACE, makeStream(array));
    }

    public static String fillWithComma(final Object... array)
    {
        return join(COMMA, makeStream(array));
    }

    public static String fillWithNewLines(final Object... array)
    {
        return join(NEWLINE, makeStream(array));
    }

    private static Stream<String> makeStream(final Object[] array)
    {
        return Arrays.stream(array).map(Object::toString);
    }

    private static String join(final String delimiter, final Stream<String> stream)
    {
        return stream.collect(Collectors.joining(delimiter));
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
