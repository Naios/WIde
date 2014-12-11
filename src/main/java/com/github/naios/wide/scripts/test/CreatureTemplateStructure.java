
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.scripts.test;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.entities.server.CreatureTemplate;
import com.github.naios.wide.core.framework.game.UnitClass;
import com.github.naios.wide.core.framework.game.UnitFlags;
import com.github.naios.wide.core.framework.storage.server.EnumValue;
import com.github.naios.wide.core.framework.storage.server.Namestorage;
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

    @ServerStorageEntry(key=true)
    @Namestorage("creature_name")
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
    @EnumValue("UnitFlags")
    private FlagProperty<UnitFlags> unit_flags;

    @Override
    public FlagProperty<UnitFlags> unit_flags()
    {
        return unit_flags;
    }

    @ServerStorageEntry
    @EnumValue("UnitClass")
    private EnumProperty<UnitClass> unit_class;

    @Override
    public EnumProperty<UnitClass> unit_class()
    {
        return unit_class;
    }
}
