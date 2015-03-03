package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Arrays;
import java.util.Objects;

import javafx.beans.binding.ListBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.github.naios.wide.api.util.Flags;
import com.github.naios.wide.api.util.LazyInitializer;
import com.github.naios.wide.api.util.StringUtil;

public class SimpleFlagProperty<T extends Enum<T>>
    extends SimpleIntegerProperty
    implements FlagProperty<T>
{
    private final Class<T> enumClass;

    private ObservableList<T> toObservableList()
    {
        return FXCollections.observableArrayList(Flags.flagSet(enumClass, get()));
    }

    private final LazyInitializer<ListProperty<T>> flagList = new LazyInitializer<>(() ->
    {
        final ListProperty<T> property = new SimpleListProperty<>(SimpleFlagProperty.this, "flagList", toObservableList());

        property.bind(new ListBinding<T>()
        {
            {
                super.bind(SimpleFlagProperty.this);
            }

            @Override
            public void dispose()
            {
                super.unbind(SimpleFlagProperty.this);
            }

            @Override
            protected ObservableList<T> computeValue()
            {
                return toObservableList();
            }

            @Override
            public ObservableList<?> getDependencies()
            {
                return FXCollections .singletonObservableList(SimpleFlagProperty.this);
            }
        });

        return property;
    });

    public SimpleFlagProperty(final Class<T> enumClass)
    {
        super();
        this.enumClass = enumClass;
    }

    public SimpleFlagProperty(final Class<T> enumClass, final int initialValue)
    {
        super(initialValue);
        this.enumClass = enumClass;
    }

    @SafeVarargs
    public SimpleFlagProperty(final Class<T> enumClass, final T... initialFlags)
    {
        this(enumClass, Flags.createFlag(initialFlags));
    }

    public SimpleFlagProperty(final Class<T> enumClass, final Object bean, final String name)
    {
        super(bean, name);
        this.enumClass = enumClass;
    }

    public SimpleFlagProperty(final Class<T> enumClass, final Object bean, final String name, final int initialValue)
    {
        super(bean, name, initialValue);
        this.enumClass = enumClass;
    }

    @SafeVarargs
    public SimpleFlagProperty(final Class<T> enumClass, final Object bean, final String name, final T... initialFlags)
    {
        this(enumClass, bean, name, Flags.createFlag(initialFlags));
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
    public String getValueAsHex()
    {
        return StringUtil.asHex(get());
    }

    @Override
    public boolean hasFlag(final T flag)
    {
        return Flags.hasFlag(flag, get());
    }

    @Override
    public ListProperty<T> flagListProperty()
    {
        return flagList.get();
    }

    @Override
    public void addFlag(final T flag)
    {
        set(Flags.addFlag(flag, get()));
    }

    @Override
    public void removeFlag(final T flag)
    {
        set(Flags.removeFlag(flag, get()));
    }

    @Override
    public String toString()
    {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder("FlagProperty [");

        result.append("class: ").append(enumClass.getSimpleName()).append(", ");

        if (Objects.nonNull(bean))
            result.append("bean: ").append(bean).append(", ");

        if (Objects.nonNull(name) && (!name.isEmpty()))
            result.append("name: ").append(name).append(", ");

        if (isBound())
        {
            result.append("bound, ");
            if (Objects.nonNull(get()))
                result.append("value: ").append(get()).append(" ")
                .append(Arrays.toString(flagListProperty().toArray()))
                .append(" ");
            else
                result.append("invalid");
        }
        else
            result.append("value: ").append(get()).append(" ")
            .append(Arrays.toString(flagListProperty().toArray()));

        result.append("]");
        return result.toString();
    }
}
