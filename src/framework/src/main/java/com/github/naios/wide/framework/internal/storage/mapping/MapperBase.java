
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.google.common.reflect.TypeToken;

public abstract class MapperBase<FROM, TO extends Mapping<BASE>, BASE extends ReadOnlyProperty<?>> implements Mapper<FROM, TO, BASE>
{
    private final MappingAdapterHolder<FROM, TO, BASE> adapterHolder;

    private final Class<? extends TO> target;

    private final Class<?>[] interfaces;

    private final Class<? extends MappingCallback<?>> implementation;

    public MapperBase(final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final Class<? extends TO> target, final List<Class<?>> interfaces,
                final Class<? extends MappingCallback<?>> implementation)
    {
        this.adapterHolder = adapterHolder;

        this.target = target;

        final List<Class<?>> i = new ArrayList<>(interfaces);
        i.add(target);

        this.interfaces = i.toArray(new Class<?>[i.size()]);

        this.implementation = implementation;
    }

    @Override
    public Mapper<FROM, TO, BASE> registerAdapter(final MappingAdapter<FROM, TO, BASE, ? extends BASE, ?> adapter)
    {
        adapterHolder.registerAdapter(adapter);
        return this;
    }

    protected MappingAdapterBridge<FROM, TO, BASE> getAdapterOf(final TypeToken<? extends BASE> type)
    {
        return adapterHolder.getAdapterOf(type);
    }

    protected Class<? extends TO> getTarget()
    {
        return target;
    }

    protected Class<?>[] getInterfaces()
    {
        return interfaces;
    }

    protected Class<?> getImplementation()
    {
        return implementation;
    }

    @SuppressWarnings("unchecked")
    private MappingCallback<TO> newImplementation()
    {
        try
        {
            return (MappingCallback<TO>) implementation.newInstance();
        }
        catch (final Exception e)
        {
            throw new Error(e.getMessage());
        }
    }

    protected abstract Mapping<BASE> newMappingBasedOn(final FROM from, TO to);

    protected abstract Mapping<BASE> newMappingBasedOn(List<Object> keys, TO to);

    @Override
    public TO map(final FROM from)
    {
        return createNewBasedOnMapping(to -> newMappingBasedOn(from, to));
    }

    @Override
    public TO createEmpty(final List<Object> keys)
    {
        return createNewBasedOnMapping(to -> newMappingBasedOn(keys, to));
    }

    private TO createNewBasedOnMapping(final Function<TO, Mapping<BASE>> createMapping)
    {
        final MappingCallback<TO> implementation = newImplementation();

        final MappingProxy proxy = new MappingProxy(implementation);

        @SuppressWarnings("unchecked")
        final TO to = (TO) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, proxy);

        proxy.setMapping(createMapping.apply(to));

        implementation.callback(to);
        return to;
    }
}
