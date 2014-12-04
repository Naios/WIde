package com.github.naios.wide.core.framework.storage.name;

public enum NameStorageType
{
    CREATURE_NAME("creature_name", new DatabaseNameStorage("creature_template", "entry", "name")),
    GAMEOBJECT_NAME("gameobject_name", new DatabaseNameStorage("gameobject_template", "entry", "name")),
    MAP_NAME("map_name", new ClientNameStorage("Map.dbc", 0, 1)),
    SPELL_NAME("spell_name", new ClientNameStorage("Spell.dbc", 0, 1));

    final String id;

    final NameStorage storage;

    NameStorageType(final String id, final NameStorage storage)
    {
        this.id = id;
        this.storage = storage;
    }

    public String getId()
    {
        return id;
    }

    public NameStorage getStorage()
    {
        return storage;
    }
}
