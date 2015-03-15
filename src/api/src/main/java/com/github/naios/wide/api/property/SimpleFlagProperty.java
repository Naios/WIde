package com.github.naios.wide.api.property;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import com.github.naios.wide.api.util.Flags;
import com.github.naios.wide.api.util.LazyInitializer;
import com.github.naios.wide.api.util.StringUtil;

public class SimpleFlagProperty<T extends Enum<T>>
    extends SimpleIntegerProperty
    implements FlagProperty<T>
{
    private final Class<T> enumClass;

    private final LazyInitializer<ObservableList<T>> flagList = new LazyInitializer<>(this::createList);

    private ObservableList<T> createList()
    {
        final ObservableList<T> list = FXCollections.observableArrayList(Flags.flagSet(getEnumClass(), get()));

        final ObjectProperty<ChangeListener<Number>> valueListener = new SimpleObjectProperty<>();
        final ObjectProperty<ListChangeListener<T>> listListener = new SimpleObjectProperty<>();

        valueListener.set(new ChangeListener<Number>()
        {
            @Override
            public void changed(final ObservableValue<? extends Number> observableValue,
                    final Number oldVal, final Number newVal)
            {
                final Collection<T> add = new HashSet<>(), remove = new HashSet<>();
                Flags.calculateDifferenceTo(getEnumClass(), oldVal.intValue(), newVal.intValue(), add, remove);

                list.removeListener(listListener.get());
                list.addAll(add);
                list.removeAll(remove);
                list.addListener(listListener.get());
            }
        });

        listListener.set(new ListChangeListener<T>()
        {
            @Override
            public void onChanged(final ListChangeListener.Change<? extends T> change)
            {
                removeListener(valueListener.get());

                while (change.next())
                {
                    change.getAddedSubList().forEach(f -> addFlag(f));
                    change.getRemoved().forEach(f -> removeFlag(f));
                }

                addListener(valueListener.get());
            }
        });

        list.addListener(listListener.get());
        addListener(valueListener.get());
        return list;
    }

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
    public ObservableList<T> getFlags()
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
    public void reset()
    {
        set(Flags.DEFAULT_VALUE);
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
                .append(Arrays.toString(getFlags().toArray()))
                .append(" ");
            else
                result.append("invalid");
        }
        else
            result.append("value: ").append(get()).append(" ")
            .append(Arrays.toString(getFlags().toArray()));

        result.append("]");
        return result.toString();
    }
}
