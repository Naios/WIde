
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javafx.beans.property.SimpleStringProperty;

public class MappingProxy implements InvocationHandler
{
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        System.out.println(String.format("DEBUG: %s", method));
        System.out.println(method.getName());
        System.out.println(method.getDeclaringClass());


        System.out.println();

        return new SimpleStringProperty("hey");
    }
}
