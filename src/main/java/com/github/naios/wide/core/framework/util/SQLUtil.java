
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

public class SQLUtil
{
    /**
     * Creates sql single or multiline comments
     */
    public static String createComment(final String text)
    {
        if (text.contains("\n"))
            return "/*\n * " + text.replaceAll("\n", "\n * ") + "\n */";
        else
            return "-- " + text;
    }
}
