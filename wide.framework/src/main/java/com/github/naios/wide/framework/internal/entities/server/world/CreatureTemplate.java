
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.entities.server.world;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.framework.internal.game.UnitClass;
import com.github.naios.wide.framework.internal.game.UnitFlags;
import com.github.naios.wide.framework.internal.storage.mapping.types.EnumProperty;
import com.github.naios.wide.framework.internal.storage.mapping.types.FlagProperty;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageKey;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageStructure;

public interface CreatureTemplate extends ServerStorageStructure
{
    public ReadOnlyIntegerProperty entry();

    public IntegerProperty kill_credit1();

    public StringProperty name();

    public FlagProperty<UnitFlags> unit_flags();

    public EnumProperty<UnitClass> unit_class();

    public static ServerStorageKey<CreatureTemplate> createKey(final int entry)
    {
        return new ServerStorageKey<CreatureTemplate>(entry);
    }
}
