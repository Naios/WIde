package com.github.naios.wide.scripts.test;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.entities.server.CreatureTemplate;
import com.github.naios.wide.core.framework.game.UnitClass;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.server.ServerStorage;
import com.github.naios.wide.core.framework.storage.server.ServerStorageEntry;
import com.github.naios.wide.core.framework.storage.server.types.EnumProperty;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;

public class CreatureTemplateStructure extends CreatureTemplate
{
    public CreatureTemplateStructure(final ServerStorage<CreatureTemplate> owner)
    {
        super(owner);
    }

    @ServerStorageEntry(key=true, metanamestorage="creature_name", varname="NPC_")
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

    // TODO is there a way to get the canonical name directly from the class?
    @ServerStorageEntry(metaenum="com.github.naios.wide.core.framework.game.UnitFlags")
    private FlagProperty<UnitFlags> unit_flags;

    @Override
    public FlagProperty<UnitFlags> unit_flags()
    {
        return unit_flags;
    }

    @ServerStorageEntry(metaenum="com.github.naios.wide.core.framework.game.UnitClass")
    private EnumProperty<UnitClass> unit_class;

    @Override
    public EnumProperty<UnitClass> unit_class()
    {
        return unit_class;
    }
}
