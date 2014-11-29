package com.github.naios.wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.entities.server.CreatureTemplate;
import com.github.naios.wide.core.framework.storage.server.ServerStorageEntry;

public class CreatureTemplateStructure extends CreatureTemplate
{
    @ServerStorageEntry(key=true)
    private ReadOnlyIntegerProperty entry;

    @Override
    public ReadOnlyIntegerProperty entry()
    {
        return entry;
    }

    @ServerStorageEntry
    private StringProperty name;

    @Override
    public StringProperty name()
    {
        return name;
    }

    @ServerStorageEntry
    private IntegerProperty unit_flags;

    @Override
    public IntegerProperty unit_flags()
    {
        return unit_flags;
    }
}
