package com.github.naios.wide.core.framework.storage.server.types;

import java.util.List;

import com.github.naios.wide.core.framework.util.FlagUtil;

import javafx.beans.property.ReadOnlyIntegerWrapper;

public class ReadOnlyFlagWrapper<T extends Enum<T>> extends ReadOnlyIntegerWrapper implements ReadOnlyFlagProperty<T>
{
    private final Class<T> type;

    public ReadOnlyFlagWrapper(final Class<T> type)
    {
        super();
        this.type = type;
    }

    public ReadOnlyFlagWrapper(final Class<T> type, final int def)
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
}
