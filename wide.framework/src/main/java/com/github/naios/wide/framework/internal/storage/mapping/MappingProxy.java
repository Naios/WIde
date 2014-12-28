
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.github.naios.wide.configuration.MappingMetaData;
import com.github.naios.wide.framework.internal.util.Pair;
import com.github.naios.wide.framework.storage.mapping.Mapping;

public class MappingProxy implements InvocationHandler
{
    private final Object implementation;

    private final Mapping<?> mapping;

    public MappingProxy(final Object implementation, final Mapping<?> mapping)
    {
        this.implementation = implementation;

        this.mapping = mapping;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        // First try to get the mapping implementation
        try
        {
            mapping.getClass().getMethod(method.getName(),
                    method.getParameterTypes());

            return method.invoke(mapping, args);
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

        final Pair<?, MappingMetaData> result = mapping.getEntryByTarget(method.getName());
        if (result != null)
            return result.first();
        else
            return new UnknownMappingEntryException(method.getName());
    }
}
