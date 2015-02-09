
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.UnknownMappingEntryException;
import com.github.naios.wide.api.util.Pair;

public class MappingProxy implements InvocationHandler
{
    private final Object implementation;

    private Optional<Mapping<?>> mapping;

    public MappingProxy(final Object implementation)
    {
        this.implementation = implementation;
        this.mapping = Optional.empty();
    }

    public MappingProxy(final Object implementation, final Mapping<?> mapping)
    {
        this.implementation = implementation;
        this.mapping = Optional.of(mapping);
    }

    public void setMapping(Mapping<?> mapping)
    {
        this.mapping = Optional.of(mapping);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        // First try to get the mapping implementation
        if (mapping.isPresent())
            try
            {
                mapping.get().getClass().getMethod(method.getName(),
                        method.getParameterTypes());

                return method.invoke(mapping.get(), args);
            } catch (final NoSuchMethodException e1)
            {
            }

        // Then try to get it in the general implementation
        try
        {
            implementation.getClass().getMethod(method.getName(),
                    method.getParameterTypes());

            return method.invoke(implementation, args);
        } catch (final NoSuchMethodException e)
        {
        }

        if (mapping.isPresent())
        {
            final Pair<?, MappingMetaData> result = mapping.get().getEntryByTarget(method.getName());
            if (result != null)
                return result.first();
        }
        return new UnknownMappingEntryException(method.getName());
    }
}
