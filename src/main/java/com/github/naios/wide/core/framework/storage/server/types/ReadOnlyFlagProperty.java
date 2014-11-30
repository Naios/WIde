package com.github.naios.wide.core.framework.storage.server.types;

import java.util.List;

public interface ReadOnlyFlagProperty<T extends Enum<T>>
{
    public int createFlag(final T flag);

    public boolean hasFlag(T flag);

    public List<T> getFlagList();
}
