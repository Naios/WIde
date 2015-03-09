
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.server.world;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.property.FlagProperty;
import com.github.naios.wide.entities.enums.Classes;

public interface QuestTemplate extends ServerStorageStructure
{
    public ReadOnlyIntegerProperty id();

    public FlagProperty<Classes> requiredClasses();

    public StringProperty title();

    public IntegerProperty prevQuestId();

    public IntegerProperty nextQuestId();
}
