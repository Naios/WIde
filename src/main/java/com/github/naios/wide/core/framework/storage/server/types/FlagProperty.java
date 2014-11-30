package com.github.naios.wide.core.framework.storage.server.types;

public interface FlagProperty<T extends Enum<T>> extends ReadOnlyFlagProperty<T>
{
    public void add(final T flag);

    public void remove(final T flag);
}
