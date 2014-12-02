package com.github.naios.wide.core.framework.storage.server.types;

import javafx.beans.property.SimpleIntegerProperty;

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

    public String asHex()
    {
        return "0x" + Integer.toHexString(get());
    }

    @Override
    public String toString()
    {
        return String.format("(%s)=%s", asHex(), type.getEnumConstants()[get()].toString());
    }
}
