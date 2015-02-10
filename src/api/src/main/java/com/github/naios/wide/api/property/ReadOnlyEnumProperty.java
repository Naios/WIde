package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;

public interface ReadOnlyEnumProperty<T extends Enum<?>>
    extends ReadOnlyProperty<T>, EnumPropertyBase<T>
{
    public ReadOnlyIntegerProperty ordinalProperty();

    public int getOrdinal();
}
