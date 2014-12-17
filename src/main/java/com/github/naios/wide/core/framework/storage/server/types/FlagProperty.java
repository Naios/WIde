
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.types;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;

import com.github.naios.wide.core.framework.util.FlagUtil;
import com.github.naios.wide.core.framework.util.StringUtil;

public class FlagProperty<T extends Enum<T>> extends SimpleIntegerProperty
{
    private final Class<T> type;

    public FlagProperty(final Class<T> type)
    {
        super();
        this.type = type;
    }

    public FlagProperty(final Class<T> type, final int def)
    {
        super(def);
        this.type = type;
    }

    public Class<T> getEnum()
    {
        return type;
    }

    public int createFlag(final T flag)
    {
        return FlagUtil.createFlag(flag);
    }

    public boolean hasFlag(final T flag)
    {
        return FlagUtil.hasFlag(flag, get());
    }

    public List<T> getFlagList()
    {
        return FlagUtil.getFlagList(type, get());
    }

    public void addFlag(final T flag)
    {
        set(get() | createFlag(flag));
    }

    public void removeFlag(final T flag)
    {
        set(get() &~ createFlag(flag));
    }

    public String asHex()
    {
        return StringUtil.asHex(get());
    }

    @Override
    public String toString()
    {
        return String.format("FlagProperty [value: %s (%s)]", Arrays.toString(getFlagList().toArray()), asHex());
    }
}
