
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;

class UnknownFormerException extends RuntimeException
{
    public UnknownFormerException(final char former)
    {
        super(String.format("Unknown DBC former %s.", former));
    }
}

public enum ClientStorageFormer
{
    FT_NA('x', Integer.BYTES, int.class),
    FT_NA_BYTE('X', 1, byte.class),
    FT_STRING('s', Integer.BYTES, String.class),
    FT_FLOAT('f', Float.BYTES, float.class),
    FT_INT('i', Integer.BYTES, int.class),
    FT_BYTE('b', 1, byte.class),
    FT_LONG('l', Long.BYTES, long.class),
    FT_SORT('d', Integer.BYTES, int.class),
    FT_IND('n', Integer.BYTES, int.class),
    FT_SQL_PRESENT('p', Integer.BYTES, int.class),
    FT_SQL_ABSENT('a', Integer.BYTES, int.class);

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
}
