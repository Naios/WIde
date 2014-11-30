package com.github.naios.wide.core.framework.storage.server.types;

import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;

import com.github.naios.wide.core.framework.util.FlagUtil;

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

    public int createFlag(final T flag)
    {
        return FlagUtil.CreateFlag(flag);
    }

    public boolean hasFlag(final T flag)
    {
        return FlagUtil.HasFlag(flag, get());
    }

    public List<T> getFlagList()
    {
        return FlagUtil.GetFlagList(type, get());
    }

    public void add(final T flag)
    {
        set(get() | createFlag(flag));
    }

    public void remove(final T flag)
    {
        set(get() &~ createFlag(flag));
    }

    @Override
    public String toString()
    {
        return Integer.toHexString(get());
    }
}
