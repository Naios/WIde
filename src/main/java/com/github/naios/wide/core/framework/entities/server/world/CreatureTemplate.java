
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.entities.server.world;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.game.UnitClass;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.types.EnumProperty;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;

public interface CreatureTemplate extends ServerStorageStructure
{
    public ReadOnlyIntegerProperty entry();

    public IntegerProperty kill_credit1();

    public StringProperty name();

    public FlagProperty<UnitFlags> unit_flags();

    public EnumProperty<UnitClass> unit_class();
}
