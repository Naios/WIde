package com.github.naios.wide.api.util;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import static java.util.Objects.isNull;

import java.util.function.Supplier;

public class LazyInitializer<T>
{
    private T object;

    private final Supplier<T> supplier;

    public LazyInitializer(final Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    public T get()
    {
        synchronized(this)
        {
            if (isNull(object))
                object = supplier.get();
        }

        return object;
    }
}