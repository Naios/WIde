package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.api.util.LazyInitializer;
import com.github.naios.wide.api.util.StringUtil;

public class SimpleEnumProperty<T extends Enum<?>>
    extends SimpleObjectProperty<T>
    implements EnumProperty<T>
{
    private final static int DEFAULT_ELEMENT = 0;

    private final Class<T> enumClass;

    private final LazyInitializer<IntegerProperty> ordinal = new LazyInitializer<>(() ->
    {
        final IntegerProperty property = new SimpleIntegerProperty(SimpleEnumProperty.this, "ordinal", getOrdinal());
        property.bind(Bindings.createIntegerBinding(() -> getOrdinal(), SimpleEnumProperty.this));
        return property;
    });

    public SimpleEnumProperty(final Class<T> enumClass)
    {
        this (enumClass, getDefaultValue(enumClass));
    }

    public SimpleEnumProperty(final Class<T> enumClass, final T initialValue)
    {
        super (initialValue);
        this.enumClass = enumClass;

        Objects.requireNonNull(initialValue);
    }

    public SimpleEnumProperty(final Class<T> enumClass, final Object bean, final String name)
    {
        super(bean, name, getDefaultValue(enumClass));
        this.enumClass = enumClass;
    }

    public SimpleEnumProperty(final Class<T> enumClass, final Object bean, final String name, final T initialValue)
    {
        super(bean, name, initialValue);
        this.enumClass = enumClass;

        Objects.requireNonNull(initialValue);
    }

    private static <T> T getDefaultValue(final Class<T> enumClass)
    {
        return enumClass.getEnumConstants()[DEFAULT_ELEMENT];
    }

    @Override
    public void set(final T newValue)
    {
        Objects.requireNonNull(newValue);
        super.set(newValue);
    }

    @Override
    public Class<T> getEnumClass()
    {
        return enumClass;
    }

    @Override
    public T[] getEnumConstants()
    {
        return getEnumClass().getEnumConstants();
    }

    @Override
    public T getEnumConstant(final int index)
    {
        return getEnumConstants()[index];
    }

    @Override
    public int getEnumClassSize()
    {
        return getEnumConstants().length;
    }

    @Override
    public ReadOnlyIntegerProperty ordinalProperty()
    {
        return ordinal.get();
    }

    @Override
    public int getOrdinal()
    {
        return get().ordinal();
    }

    @Override
    public String toString()
    {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder("EnumProperty [");

        result.append("class: ").append(enumClass.getSimpleName()).append(", ");

        if (Objects.nonNull(bean))
            result.append("bean: ").append(bean).append(", ");

        if (Objects.nonNull(name) && (!name.isEmpty()))
            result.append("name: ").append(name).append(", ");

        if (isBound())
        {
            result.append("bound, ");
            if (Objects.nonNull(get()))
                result.append("value: ").append(get());
            else
                result.append("invalid");
        }
        else
            result.append("value: ").append(get());

        result.append("]");
        return result.toString();
    }

    @Override
    public String getValueAsHex()
    {
        return StringUtil.asHex(getOrdinal());
    }
}
