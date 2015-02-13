
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructurePublicBase;
import com.github.naios.wide.api.util.Pair;

public interface ServerStorageStructurePrivateBase extends ServerStorageStructurePublicBase
{
    public void setOwnerAndTracker(final ServerStorageImpl<?> owner, final ChangeTrackerImpl changeTracker);

    public void onCreate();

    public void onDelete();

    public void onUpdate(final ReadOnlyProperty<?> property, final Object oldValue);
}
