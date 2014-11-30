package com.github.naios.wide.core.framework.storage.server.types;

import java.util.List;

public interface FlagProperty<T extends Enum<T>>
{
    public int createFlag(final T flag);

    public boolean hasFlag(T flag);

    public void add(final T flag);

    public void remove(final T flag);

    public List<T> getFlags();
}
