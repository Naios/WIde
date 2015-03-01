
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.github.naios.wide.api.util.Pair;

public class ClientStorageFormatImpl implements ClientStorageFormat
{
    private final String format, comment;

    private final int size;

    private final Map<Integer, Integer> indexToOffsetCache =
            new HashMap<>();

    private final Set<Integer> entries =
            new TreeSet<>();

    public ClientStorageFormatImpl(final String format)
    {
        this (format, "");
    }

    public ClientStorageFormatImpl(final String format, final String comment)
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

    @Override
    public int getByteSize()
    {
        return size;
    }

    @Override
    public String getFormat()
    {
        return format;
    }

    @Override
    public int size()
    {
        return entries.size();
    }

    @Override
    public ClientStorageFormer getFormerAtIndex(final int index)
    {
        return ClientStorageFormer.getFormerOfCharacter(format.charAt(index));
    }

    @Override
    public int getOffsetOfIndex(final int index)
    {
        return indexToOffsetCache.get(index);
    }

    @Override
    public Iterator<Pair<Integer, ClientStorageFormer>> iterator()
    {
        return new Iterator<Pair<Integer, ClientStorageFormer>>()
        {
            Iterator<Integer> iter = entries.iterator();

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public Pair<Integer, ClientStorageFormer> next()
            {
                final int idex = iter.next();
                return new Pair<>(idex, getFormerAtIndex(idex));
            }
        };
    }

    @Override
    public String toString()
    {
        return String.format("Format: \"%s\"%s%s", format, comment.isEmpty() ? "" : (" (" + comment + ") "),
                indexToOffsetCache.keySet().stream()
                .map(index -> String.format("\n  %-3s %s", index, getFormerAtIndex(index)))
                .collect(Collectors.joining()));
    }
}
