
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.game.UnitClass;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.server.types.EnumProperty;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;

public abstract class ReducedCreatureTemplate implements ServerTable
{
    public abstract ReadOnlyIntegerProperty entry();

    public abstract StringProperty name();

    public abstract IntegerProperty kill_credit1();

    public abstract FlagProperty<UnitFlags> unit_flags();

    public abstract EnumProperty<UnitClass> unit_class();
}
