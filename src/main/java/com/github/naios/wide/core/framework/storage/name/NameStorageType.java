package com.github.naios.wide.core.framework.storage.name;

public enum NameStorageType
{
    CREATURE_NAME("creature_name", "NPC", new DatabaseNameStorage("creature_template", "entry", "name")),
    GAMEOBJECT_NAME("gameobject_name", "GOB", new DatabaseNameStorage("gameobject_template", "entry", "name")),
    MAP_NAME("map_name", "MAP", new ClientNameStorage("Map.dbc", 0, 1)),
    SPELL_NAME("spell_name", "SPELL", new ClientNameStorage("Spell.dbc", 0, 1));

    final String id, prefix;

    final NameStorage storage;

    NameStorageType(final String id, final String prefix, final NameStorage storage)
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
        return prefix;
    }

    public NameStorage getStorage()
    {
        return storage;
    }
}
