
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping.types;

import javafx.beans.property.ReadOnlyProperty;

public interface ReadOnlyEnumProperty<T extends Enum<T>>
    extends ReadOnlyProperty<Number>
{
    public Class<T> getEnum();

    public boolean is(T value);
}
