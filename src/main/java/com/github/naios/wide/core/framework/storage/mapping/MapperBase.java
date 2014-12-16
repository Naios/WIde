
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.Proxy;

import com.google.common.reflect.TypeToken;

public abstract class MapperBase<FROM, TO extends Mapping<BASE>, BASE> implements Mapper<FROM, TO, BASE>
{
    private final MappingAdapterHolder<FROM, TO, BASE> adapterHolder;

    private final Class<? extends TO> target;

    private final Class<?> implementation;

    public MapperBase(final Class<? extends TO> target, final Class<?> implementation)
    {
        this (new MappingAdapterHolder<>(), target, implementation);
    }

    public MapperBase(final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final Class<? extends TO> target, final Class<?> implementation)
    {
        this.adapterHolder = adapterHolder;

        this.target = target;

        this.implementation = implementation;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mapper<FROM, TO, BASE> registerAdapter(final TypeToken type,
            final MappingAdapter<FROM, ? extends BASE> adapter)
    {
        adapterHolder.registerAdapter(type, adapter);
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected MappingAdapter<FROM, BASE> getAdapterOf(final TypeToken type)
    {
        return adapterHolder.getAdapterOf(type);
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

    protected Class<? extends TO> getTarget()
    {
        return target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TO map(final FROM from)
    {
        final Mapping<BASE> mapping = newMappingBasedOn(from);
        final MappingProxy proxy = new MappingProxy(newImplementation(), mapping);

        return (TO) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {target}, proxy);
    }
}
