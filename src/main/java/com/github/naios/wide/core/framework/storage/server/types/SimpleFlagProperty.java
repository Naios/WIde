package com.github.naios.wide.core.framework.storage.server.types;

import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;

import com.github.naios.wide.core.framework.util.FlagUtil;

public class SimpleFlagProperty<T extends Enum<T>> extends SimpleIntegerProperty implements FlagProperty<T>
{
    private final Class<T> type;

    public SimpleFlagProperty(final Class<T> type)
    {
        super();
        this.type = type;
    }

    public SimpleFlagProperty(final Class<T> type, final int def)
    {
        super(def);
        this.type = type;
    }

    @Override
    public int createFlag(final T flag)
    {
        return FlagUtil.CreateFlag(flag);
    }

    @Override
    public boolean hasFlag(final T flag)
    {
        return FlagUtil.HasFlag(flag, get());
    }

    @Override
    public List<T> getFlagList()
    {
        return FlagUtil.GetFlagList(type, get());
    }

    @Override
    public void add(final T flag)
    {
        set(get() | createFlag(flag));
    }

    @Override
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
