
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.name;

public enum NameStorageType
{
    CREATURE_NAME("creature_name", "NPC", new DatabaseNameStorage("creature_template", "entry", "name")),
    GAMEOBJECT_NAME("gameobject_name", "GOB", new DatabaseNameStorage("gameobject_template", "entry", "name")),
    MAP_NAME("map_name", "MAP", new ClientNameStorage("Map.dbc", 0, 1)),
    SPELL_NAME("spell_name", "SPELL", new ClientNameStorage("Spell.dbc", 0, 1));

    private final String PREFIX_DELEMITER= "_";

    final String id, prefix;

    final NameStorage storage;

    private NameStorageType(final String id, final String prefix, final NameStorage storage)
    {
        this.id = id;
        this.prefix = prefix;
        this.storage = storage;
    }

    public String getId()
    {
        return id;
    }

    public String getPrefix()
    {
        return prefix + PREFIX_DELEMITER;
    }

    public NameStorage getStorage()
    {
        return storage;
    }
}
