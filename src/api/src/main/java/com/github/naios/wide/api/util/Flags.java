package com.github.naios.wide.api.util;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class Flags
{
    public static final int DEFAULT_VALUE = 0;

    private Flags() { }

    @SafeVarargs
    public static <T extends Enum<T>> int createFlag(final T... flags)
    {
        int value = 0;
        for (final T flag : flags)
            value |= 1 << flag.ordinal();

        return value;
    }

    public static <T extends Enum<T>> boolean hasFlag(final T flag, final int mask)
    {
        return (mask & createFlag(flag)) != 0;
    }

    public static <T extends Enum<T>> int addFlag(final T flag, final int mask)
    {
        return mask | createFlag(flag);
    }

    public static <T extends Enum<T>> int removeFlag(final T flag, final int mask)
    {
        return mask &~ createFlag(flag);
    }

    public static <T extends Enum<T>> List<T> createFlagList(final Class<T> type, final int mask)
    {
        final List<T> list = new LinkedList<>();
        for (final T flag : type.getEnumConstants())
            if (hasFlag(flag, mask))
                list.add(flag);

        return list;
    }

    public static <T extends Enum<T>> void calculateDifferenceTo(final Class<T> enumClass,
            final int oldMask, final int newMask,
            final Collection<T> add, final Collection<T> remove)
    {
        final List<T> currentFlags = Flags.createFlagList(enumClass, oldMask);
        final List<T> oldFlags = Flags.createFlagList(enumClass, newMask);

        currentFlags
            .stream()
            .filter(entry-> !oldFlags.contains(entry))
            .forEach(entry -> add.add(entry));

        oldFlags
            .stream()
            .filter(entry-> !currentFlags.contains(entry))
            .forEach(entry -> remove.add(entry));
    }
}
