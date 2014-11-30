package com.github.naios.wide.core.framework.util;

import java.util.LinkedList;
import java.util.List;

public class FlagUtil
{
    public static <T extends Enum<T>> int CreateFlag(final T flag)
    {
        return 1 << flag.ordinal();
    }

    public static <T extends Enum<T>> boolean HasFlag(final T flag, final int mask)
    {
        final int i = CreateFlag(flag);
        return (mask & i) == i;
    }

    public static <T extends Enum<T>> List<T> GetFlagList(final Class<T> type, final int mask)
    {
        final List<T> list = new LinkedList<T>();
        for (final T flag : type.getEnumConstants())
            if (HasFlag(flag, mask))
                list.add(flag);

        return list;
    }
}
