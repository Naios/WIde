
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

import com.github.naios.wide.core.framework.util.CrossIterator;
import com.github.naios.wide.core.framework.util.Pair;
import com.github.naios.wide.core.framework.util.StringUtil;

public class ClientStorageFormat implements Iterable<Pair<Integer, ClientStorageFormer>>
{
    private final String format, comment;

    private final int size;

    private final Map<Integer, Integer> indexToOffsetCache =
            new HashMap<>();

    public ClientStorageFormat(final String format)
    {
        this (format, "");
    }

    public ClientStorageFormat(final String format, final String comment)
    {
        this.format = format;

        this.comment = comment;

        int offset = 0;
        for (final Pair<Integer, ClientStorageFormer> entry : this)
        {
            indexToOffsetCache.put(entry.first(), offset);
            offset += entry.second().getSize();
        }

        this.size = offset;
    }

    public int byteSize()
    {
        return size;
    }

    public String getFormat()
    {
        return format;
    }

    public int formatLength()
    {
        return format.length();
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
    public Iterator<Pair<Integer, ClientStorageFormer>> iterator()
    {
        return new Iterator<Pair<Integer, ClientStorageFormer>>()
        {
            private int pos = 0;

            @Override
            public boolean hasNext()
            {
                return pos < formatLength();
            }

            @Override
            public Pair<Integer, ClientStorageFormer> next()
            {
                return new Pair<>(pos, getFormerAtIndex(pos++));
            }
        };
    }

    @Override
    public String toString()
    {
        return String.format("Format: \"%s\"%s%s", format, comment.isEmpty() ? "" : (" (" + comment + ") "),
                StringUtil.concat(new CrossIterator<Pair<Integer, ClientStorageFormer>, String>(this,
                        entry -> String.format("\n  %-3s %s",
                                entry.first(), entry.second()))));
    }
}
