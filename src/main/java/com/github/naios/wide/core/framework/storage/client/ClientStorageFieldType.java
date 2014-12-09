
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.util.function.BiPredicate;

// TODO Is there any type in the JDK that already implements this?
// The order of the types is important
// It checks the types ordered ascend
public enum ClientStorageFieldType
{
    STRING(String.class,
    (storage, column) ->
    {
        for (int y = 0; (y < storage.recordsCount) && (y < ClientStorage.STRING_CHECK_MAX_RECORDS); ++y)
        {
            final int offset = storage.buffer.getInt(storage.getOffset(y, column));
            if (!storage.offsetToStringCache.containsKey(offset + storage.getStringBlockOffset()))
                return false;
        }
        return true;
    }),
    BOOLEAN(boolean.class,
    (storage, column) ->
    {
        for (int y = 0; y < storage.recordsCount; ++y)
        {
            final int value = storage.buffer.getInt(storage.getOffset(y, column));
            if (value != 0 || value != 1)
                return false;
        }
        return true;
    }),
    FLOAT(float.class,
    (storage, column) ->
    {
        int match = 0, ignore = 0;

        for (int y = 0; y < storage.recordsCount; ++y)
        {
            final int value = storage.buffer.getInt(storage.getOffset(y, column));
            if ((value < -ClientStorage.FLOAT_CHECK_BOUNDS) || (value > ClientStorage.FLOAT_CHECK_BOUNDS))
                ++match;

            if (value == 0)
                ++ignore;
        }

        final float ratio = (match) / ((float)storage.recordsCount - ignore);
        return ratio >= ClientStorage.FLOAT_CHECK_PERCENTAGE;
    }),
    INTEGER(int.class,
    (storage, column) ->
    {
        return true;
    }),
    UNKNOWN(int.class,
    (storage, column) ->
    {
        // Should never happen.
        assert false;
        return true;
    });

    private final Class<?> type;

    final BiPredicate<ClientStorage<?>, Integer /*column*/> check;

    ClientStorageFieldType(final Class<?> type, final BiPredicate<ClientStorage<?>, Integer> check)
    {
        this.type = type;
        this.check = check;
    }

    public Class<?> getType()
    {
        return type;
    }

    public BiPredicate<ClientStorage<?>, Integer> getCheck()
    {
        return check;
    }
}
