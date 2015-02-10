package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import javafx.beans.property.Property;
import javafx.beans.value.WritableObjectValue;

public interface EnumProperty<T extends Enum<?>>
    extends Property<T>, WritableObjectValue<T>, ReadOnlyEnumProperty<T>
{
}
