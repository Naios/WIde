package com.github.naios.wide.core.framework.storage.server.types;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;

public class SimpelFlagProperty<T extends Enum<T>> extends SimpleIntegerProperty implements FlagProperty<T>
{
    private final Class<T> type;

    public SimpelFlagProperty(final Class<T> type)
    {
        super();
        this.type = type;
    }

    public SimpelFlagProperty(final Class<T> type, final int def)
    {
        super(def);
        this.type = type;
    }

    @Override
    public int createFlag(final T flag)
    {
        return 1 << flag.ordinal();
    }

    @Override
    public boolean hasFlag(final T flag)
    {
        final int i = createFlag(flag);
        return (get() & i) == i;
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

    @Override
    public List<T> getFlags()
    {
        final List<T> list = new LinkedList<T>();
        for (final T flag : type.getEnumConstants())
            if (hasFlag(flag))
                list.add(flag);

        return list;
    }
}
