
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.server;

import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.entities.internal.ServerStorageKeyImpl;
import com.github.naios.wide.entities.server.world.CreatureTemplate;
import com.github.naios.wide.entities.server.world.QuestTemplate;

public final class ServerStorageKeys
{
    private ServerStorageKeys() { }

    public static ServerStorageKey<CreatureTemplate> ofCreatureTemplate(final int entry)
    {
        return new ServerStorageKeyImpl<>(entry);
    }

    public static ServerStorageKey<QuestTemplate> ofQuestTemplate(final int id)
    {
        return new ServerStorageKeyImpl<>(id);
    }
}
