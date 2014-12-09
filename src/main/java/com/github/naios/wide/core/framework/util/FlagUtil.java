
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

import java.util.LinkedList;
import java.util.List;

public class FlagUtil
{
    public static <T extends Enum<T>> int createFlag(final T flag)
    {
        return 1 << flag.ordinal();
    }

    public static <T extends Enum<T>> boolean hasFlag(final T flag, final int mask)
    {
        return (mask & createFlag(flag)) != 0;
    }

    public static <T extends Enum<T>> List<T> getFlagList(final Class<T> type, final int mask)
    {
        final List<T> list = new LinkedList<T>();
        for (final T flag : type.getEnumConstants())
            if (hasFlag(flag, mask))
                list.add(flag);

        return list;
    }
}
