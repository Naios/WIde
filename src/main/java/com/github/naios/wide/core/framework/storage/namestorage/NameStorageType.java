package com.github.naios.wide.core.framework.storage.namestorage;

public enum NameStorageType
{
    CREATURE_NAME("creature_name"),
    GAMEOBJECT_NAME("gameobject_name"),
    SPELL_NAME("spell_name");

    final String id;

    NameStorageType(final String id)
    {
        this.id = id;
    }
}
