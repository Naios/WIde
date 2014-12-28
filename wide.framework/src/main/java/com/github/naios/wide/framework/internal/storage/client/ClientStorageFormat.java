
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.naios.wide.framework.internal.util.CrossIterator;
import com.github.naios.wide.framework.internal.util.Pair;
import com.github.naios.wide.framework.internal.util.StringUtil;

public class ClientStorageFormat implements Iterable<Pair<Integer, ClientStorageFormer>>
{
    private final String format, comment;

    private final int size;

    private final Map<Integer, Integer> indexToOffsetCache =
            new HashMap<>();

    private final Set<Integer> entries =
            new TreeSet<>();

    public ClientStorageFormat(final String format)
    {
        this (format, "");
    }

    public ClientStorageFormat(final String format, final String comment)
    {
        this.format = format;

        this.comment = comment;

        int offset = 0;
        for (int i = 0; i < format.length(); ++i)
        {
            indexToOffsetCache.put(i, offset);
            final ClientStorageFormer former = getFormerAtIndex(i);

            if (former.isPresent())
                entries.add(i);

            offset += former.getSize();
        }

        this.size = offset;
    }

    public int getByteSize()
    {
        return size;
    }

    public String getFormat()
    {
        return format;
    }

    public int size()
    {
        return entries.size();
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
        return new CrossIterator<>(entries, (index) -> new Pair<>(index, getFormerAtIndex(index)));
    }

    @Override
    public String toString()
    {
        return String.format("Format: \"%s\"%s%s", format, comment.isEmpty() ? "" : (" (" + comment + ") "),
                StringUtil.concat(new CrossIterator<>(indexToOffsetCache.keySet(),
                        index -> String.format("\n  %-3s %s",
                                index, getFormerAtIndex(index)))));
    }
}
