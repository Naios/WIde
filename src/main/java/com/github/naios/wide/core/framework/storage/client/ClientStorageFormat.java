
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientStorageFormat implements Iterable<ClientStorageFormer>
{
    private final String format;

    private final Map<Integer, Integer> indexToOffsetCache =
            new HashMap<>();

    public ClientStorageFormat(final String format)
    {
        this.format = format;

        int offset = 0, i = 0;
        for (final ClientStorageFormer former : this)
        {
            indexToOffsetCache.put(i++, offset);
            offset += former.getSize();
        }
    }

    public ClientStorageFormer getFormerAtIndex(final int index)
    {
        return ClientStorageFormer.getFormerOfCharacter(format.charAt(index));
    }

    public int getOffsetOfIndex(final int index)
    {
        return indexToOffsetCache.get(index);
    }

    @Override
    public Iterator<ClientStorageFormer> iterator()
    {
        return new Iterator<ClientStorageFormer>()
        {
            private int pos = 0;

            @Override
            public boolean hasNext()
            {
                return pos < format.length();
            }

            @Override
            public ClientStorageFormer next()
            {
                return getFormerAtIndex(pos++);
            }
        };
    }
}
