package com.github.naios.wide.core.framework.entities.server;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;

@StorageName(name="creature_template")
public abstract class CreatureTemplate extends ServerStorageStructure
{
    public CreatureTemplate(final ServerStorage<CreatureTemplate> owner)
    {
        super(owner);
    }

    public abstract ReadOnlyIntegerProperty entry();

    public abstract StringProperty name();

    public abstract IntegerProperty unit_flags();

    public static ServerStorageKey<CreatureTemplate> CreateKey(final int entry)
    {
        return new ServerStorageKey<CreatureTemplate>(entry);
    }
}
