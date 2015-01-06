
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

import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.google.common.reflect.TypeToken;

public abstract class MapperBase<FROM, TO extends Mapping<BASE>, BASE> implements Mapper<FROM, TO, BASE>
{
    private final MappingAdapterHolder<FROM, TO, BASE> adapterHolder;

    private final Class<? extends TO> target;

    private final Class<?>[] interfaces;

    @SuppressWarnings("rawtypes")
    private final Class<? extends MappingImplementation> implementation;

    public MapperBase(final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final Class<? extends TO> target, final List<Class<?>> interfaces,
                @SuppressWarnings("rawtypes") final Class<? extends MappingImplementation> implementation)
    {
        this.adapterHolder = adapterHolder;

        this.target = target;

        final List<Class<?>> i = new ArrayList<>(interfaces);
        i.add(target);

        this.interfaces = i.toArray(new Class<?>[i.size()]);

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
    private MappingImplementation<TO> newImplementation()
    {
        try
        {
            return implementation.newInstance();
        }
        catch (final Exception e)
        {
            throw new Error(e.getMessage());
        }
    }

    protected abstract Mapping<BASE> newMappingBasedOn(final FROM from);

    protected abstract Mapping<BASE> newMappingBasedOn(List<Object> keys);

    @Override
    public TO map(final FROM from)
    {
        return createNewBasedOnMapping(newMappingBasedOn(from));
    }

    @Override
    public TO createEmpty(final List<Object> keys)
    {
        return createNewBasedOnMapping(newMappingBasedOn(keys));
    }

    @SuppressWarnings("unchecked")
    private TO createNewBasedOnMapping(final Mapping<BASE> mapping)
    {
        final MappingImplementation<TO> implementation = newImplementation();

        final MappingProxy proxy = new MappingProxy(implementation, mapping);

        final TO to = (TO) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, proxy);

        implementation.callback(to);
        return to;
    }
}
