package com.github.naios.wide.api.util;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

public final class Flags
{
    public static final int DEFAULT_VALUE = 0;

    private Flags() { }

    @SafeVarargs
    public static <T extends Enum<?>> int createFlag(final T... flags)
    {
        int value = 0;
        for (final T flag : flags)
            value |= 1 << flag.ordinal();

        return value;
    }

    public static <T extends Enum<?>> boolean hasFlag(final T flag, final int mask)
    {
        return (mask & createFlag(flag)) != 0;
    }

    public static <T extends Enum<?>> int addFlag(final T flag, final int mask)
    {
        return mask | createFlag(flag);
    }

    public static <T extends Enum<?>> int removeFlag(final T flag, final int mask)
    {
        return mask &~ createFlag(flag);
    }

    public static <T extends Enum<?>> Set<T> flagSet(final Class<T> type, final int mask)
    {
        final Set<T> set = new TreeSet<>();
        for (final T flag : type.getEnumConstants())
            if (hasFlag(flag, mask))
                set.add(flag);

        return set;
    }

    public static <T extends Enum<?>> void calculateDifferenceTo(final Class<T> enumClass,
            final int oldMask, final int newMask,
            final Collection<T> add, final Collection<T> remove)
    {
        final Set<T> currentFlags = Flags.flagSet(enumClass, newMask);
        final Set<T> oldFlags = Flags.flagSet(enumClass, oldMask);

        add.addAll(Sets.difference(currentFlags, oldFlags));
        remove.addAll(Sets.difference(oldFlags, currentFlags));
    }
}
