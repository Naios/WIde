
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;

public abstract class MapperBase<FROM, TO extends Mapping<BASE>, BASE> implements Mapper<FROM, TO, BASE>
{
    private final Map<TypeToken<? extends BASE>,  MappingAdapter<FROM, ? extends BASE>> adapter =
            new HashMap<>();

    private final Class<? extends TO> target;

    private final List<Class<?>> interfaces;

    private final Class<?> implementation;

    public MapperBase(final Class<? extends TO> target, final List<Class<?>> interfaces,
            final Class<?> implementation)
    {
        this.target = target;

        this.interfaces = new ArrayList<>(interfaces);
        this.interfaces.add(target);

        this.implementation = implementation;
    }

    @Override
    public Mapper<FROM, TO, BASE> registerAdapter(final TypeToken<? extends BASE> type,
            final MappingAdapter<FROM, ? extends BASE> adapter)
    {
        this.adapter.put(type, adapter);
        return this;
    }

    protected MappingAdapter<FROM, ? extends BASE> getAdapterOf(final TypeToken<? extends BASE> type)
    {
        return adapter.get(type);
    }

    protected List<Class<?>> getInterfaces()
    {
        return interfaces;
    }

    protected Class<?>[] getInterfacesAsArray()
    {
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

    protected Class<?> getImplementation()
    {
        return implementation;
    }

    private Object newImplementation()
    {
        try
        {
            return implementation.newInstance();
        }
        catch (final Exception e)
        {
            return new Error(e.getMessage());
        }
    }

    protected abstract Mapping<BASE> newMappingBasedOn(final FROM from);

    @SuppressWarnings("unchecked")
    @Override
    public TO map(final FROM from)
    {
        final Mapping<BASE> mapping = newMappingBasedOn(from);
        final MappingProxy proxy = new MappingProxy(newImplementation(), mapping);

        return (TO) Proxy.newProxyInstance(getClass().getClassLoader(), getInterfacesAsArray(), proxy);
    }
}