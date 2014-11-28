package com.github.naios.wide.scripts.test;

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
