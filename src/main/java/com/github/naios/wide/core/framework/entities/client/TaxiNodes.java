
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.entities.client;

import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;

@StorageName(name="TaxiNodes.db2")
public abstract class TaxiNodes extends ClientStorageStructure
{
    public abstract int getEntry();

    public abstract int getMap();

    public abstract float getX();

    public abstract float getY();

    public abstract float getZ();
}
