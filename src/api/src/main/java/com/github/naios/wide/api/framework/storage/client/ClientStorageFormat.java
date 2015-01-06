
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.client;

import java.util.Iterator;

import com.github.naios.wide.api.util.Pair;

public interface ClientStorageFormat extends Iterable<Pair<Integer, ClientStorageFormer>>
{
    public int getByteSize();

    public String getFormat();

    public int size();

    public ClientStorageFormer getFormerAtIndex(int index);

    public int getOffsetOfIndex(int index);

    @Override
    public Iterator<Pair<Integer, ClientStorageFormer>> iterator();
}
