
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdentitySet<T> implements Set<T>
{
    private final Map<T, Object> storage =
            new IdentityHashMap<>();

    private static final Object DUMMY = new Object();

    @Override
    public int size()
    {
        return storage.size();
    }

    @Override
    public boolean isEmpty()
    {
        return storage.isEmpty();
    }

    @Override
    public boolean contains(final Object o)
    {
        return storage.containsKey(o);
    }

    @Override
    public Iterator<T> iterator()
    {
        return storage.keySet().iterator();
    }

    @Override
    public Object[] toArray()
    {
        return storage.keySet().toArray();
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    @Override
    public <T> T[] toArray(final T[] a)
    {
        return (T[]) storage.keySet().toArray();
    }

    @Override
    public boolean add(final T e)
    {
        storage.put(e, DUMMY);
        return true;
    }

    @Override
    public boolean remove(final Object o)
    {
        storage.remove(o);
        return true;
    }

    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return storage.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c)
    {
        c.forEach((element) -> { add(element); });
        return true;
    }

    @Override
    public boolean retainAll(final Collection<?> c)
    {
        storage.forEach((element, dummy) -> { if (!c.contains(element)) storage.remove(element); });
        return true;
    }

    @Override
    public boolean removeAll(final Collection<?> c)
    {
        c.forEach((element) -> { storage.remove(c); });
        return true;
    }

    @Override
    public void clear()
    {
        storage.clear();
    }
}
