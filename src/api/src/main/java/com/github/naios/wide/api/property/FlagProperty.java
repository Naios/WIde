package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import javafx.beans.property.Property;
import javafx.beans.value.WritableIntegerValue;

public interface FlagProperty<T extends Enum<T>>
    extends Property<Number>, WritableIntegerValue, EnumPropertyBase<T>, ReadOnlyFlagProperty<T>
{
    public void addFlag(final T flag);

    public void removeFlag(final T flag);

    public void reset();
}
