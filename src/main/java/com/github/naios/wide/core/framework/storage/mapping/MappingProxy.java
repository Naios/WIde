
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MappingProxy implements InvocationHandler
{
    private final Object implementation;

    public MappingProxy(final Object implementation)
    {
        this.implementation = implementation;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        try
        {
            implementation.getClass().getMethod(method.getName(), method.getParameterTypes());
            return method.invoke(implementation, args);
        }
        catch (final NoSuchMethodException e)
        {
        }

        System.out.println(String.format("Method: %s not found!", method.getName()));
        System.out.println(String.format("Probably requested %s value.", method.getName()));

        return null;
    }
}
