
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.entities.client;

import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;

public interface TaxiNodes extends ClientStorageStructure
{
    public ReadOnlyIntegerProperty getEntry();

    public ReadOnlyIntegerProperty getMap();

    public ReadOnlyFloatProperty getX();

    public ReadOnlyFloatProperty getY();

    public ReadOnlyFloatProperty getZ();
}
