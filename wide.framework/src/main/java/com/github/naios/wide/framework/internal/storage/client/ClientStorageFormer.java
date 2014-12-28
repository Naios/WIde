
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
class UnknownFormerException extends RuntimeException
{
    public UnknownFormerException(final char former)
    {
        super(String.format("Unknown DBC former %s.", former));
    }
}

public enum ClientStorageFormer
{
    /**
     * not used or unknown, 4 byte size
     */
    FT_NA('x', Integer.BYTES, Void.class),

    /**
     * not used or unknown, byte
     */
    FT_NA_BYTE('X', 1, Void.class),

    /**
     * string
     */
    FT_STRING('s', Integer.BYTES, String.class),

    /**
     * float
     */
    FT_FLOAT('f', Float.BYTES, float.class),

    /**
     * uint32
     */
    FT_INT('i', Integer.BYTES, int.class),

    /**
     * uint8
     */
    FT_BYTE('b', 1, byte.class),

    /**
     * uint64
     */
    FT_LONG('l', Long.BYTES, long.class),

    /**
     * sorted by this field, field is not included
     */
    FT_SORT('d', Integer.BYTES, Void.class),

    /**
     * sorted by this field and parsed to data
     */
    FT_IND('n', Integer.BYTES, int.class),

    /**
     * Used in sql format to mark column present in sql dbc
     */
    FT_SQL_PRESENT('p', Integer.BYTES, Void.class),

    /**
     * Used in sql format to mark column absent in sql dbc
     */
    FT_SQL_ABSENT('a', Integer.BYTES, Void.class);

    private final char former;

    private final int size;

    private final TypeToken<?> type;

    private static Map<Character, ClientStorageFormer> charToFormer = createCache();

    private ClientStorageFormer(final char former, final int size, final Class<?> type)
    {
        this.former = former;

        this.size = size;

        this.type = TypeToken.of(type);
    }

    public char getFormer()
    {
        return former;
    }

    public int getSize()
    {
        return size;
    }

    public TypeToken<?> getType()
    {
        return type;
    }

    public boolean isPresent()
    {
        return !getType().equals(FT_NA.getType());
    }

    public static ClientStorageFormer getFormerOfCharacter(final char former)
    {
        if (!charToFormer.containsKey(former))
            throw new UnknownFormerException(former);

        return charToFormer.get(former);
    }

    private static Map<Character, ClientStorageFormer> createCache()
    {
        final Map<Character, ClientStorageFormer> map = new HashMap<>();

        for (final ClientStorageFormer former : ClientStorageFormer.values())
            map.put(former.former, former);

        return map;
    }

    @Override
    public String toString()
    {
        return String.format("%-10s (\'%s\' -> %s)",
                name(), former, type.getRawType().getSimpleName());
    }
}
