
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.types;

import javafx.beans.property.SimpleIntegerProperty;

import com.github.naios.wide.core.Constants;

public class EnumProperty<T extends Enum<T>> extends SimpleIntegerProperty
{
    private final Class<T> type;

    public EnumProperty(final Class<T> type)
    {
        super();
        this.type = type;
    }

    public EnumProperty(final Class<T> type, final int def)
    {
        super(def);
        this.type = type;
    }

    public Class<T> getEnum()
    {
        return type;
    }

    public void set(final T value)
    {
        set(value.ordinal());
    }

    public boolean is(final T value)
    {
        return get() == value.ordinal();
    }

    @Override
    public String toString()
    {
        String enumName;

        try
        {
            enumName = type.getEnumConstants()[get()].toString();
        }
        catch (final Exception e)
        {
            enumName = Constants.STRING_MISSIN_ENTRY.toString();
        }

        return String.format("EnumProperty [value: %s (%s)]", enumName, get());
    }
}
