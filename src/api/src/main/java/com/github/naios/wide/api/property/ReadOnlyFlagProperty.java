package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import javafx.beans.binding.NumberExpression;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

public interface ReadOnlyFlagProperty<T extends Enum<T>>
    extends ReadOnlyProperty<Number>, NumberExpression, EnumPropertyBase<T>
{
    public boolean hasFlag(final T flag);

    public ReadOnlyListProperty<T> flagListProperty();
}
