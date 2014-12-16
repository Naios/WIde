
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.github.naios.wide.core.framework.util.Pair;

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
        // First try to get the implementation in the general implementation
        try
        {
            implementation.getClass().getMethod(method.getName(), method.getParameterTypes());
            return method.invoke(implementation, args);
        }
        catch (final NoSuchMethodException e)
        {
            // Then try to get it in the mapping implementation
            try
            {
                mapping.getClass().getMethod(method.getName(), method.getParameterTypes());
                return method.invoke(mapping, args);
            }
            catch (final NoSuchMethodException e1)
            {
            }
        }

        final Pair<?, MappingMetaData> result = mapping.getEntryByName(method.getName());
        if (result != null)
            return result.first();
        else
            return new UnknownMappingEntryException(method.getName());
    }
}
