
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.name;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Simple fast namestorages where we need no database mapping
 * For Example CreatureTemplate -> {Entry, Name}
 */
public abstract class NameStorage
{
    private final Map<Integer, String> storage =
            new HashMap<>();

    // Setups some storage specific stuff
    public void setup()
    {
    }

    public String request(final int entry)
    {
        final String name = storage.get(entry);
        if (name == null)
            return fail(entry);
        else
            return name;
    }

    // Return a string on fail (example: unknown item 2625)
    public String fail(final int entry)
    {
        return null;
    }

    protected void add(final int entry, final String name)
    {
        storage.put(entry, name);
    }

    // Loads the storage into the cache
    public abstract void load();

    public void unload()
    {
        storage.clear();
    }

    @Override
    public String toString()
    {
        return storage.toString();
    }
}
