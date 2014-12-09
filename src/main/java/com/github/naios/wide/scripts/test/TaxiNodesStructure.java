
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

import com.github.naios.wide.core.framework.entities.client.TaxiNodes;
import com.github.naios.wide.core.framework.storage.client.ClientStorageEntry;

public class TaxiNodesStructure extends TaxiNodes
{
    @ClientStorageEntry(idx=0, key=true)
    private int entry;

    @Override
    public int getEntry()
    {
        return entry;
    }

    @ClientStorageEntry(idx=1)
    private int map;

    @Override
    public int getMap()
    {
        return map;
    }

    @ClientStorageEntry(idx=2)
    private float x;

    @Override
    public float getX()
    {
        return 0;
    }

    @ClientStorageEntry(idx=3)
    private float y;

    @Override
    public float getY()
    {
        return y;
    }

    @ClientStorageEntry(idx=4)
    private float z;

    @Override
    public float getZ()
    {
        return z;
    }
}
