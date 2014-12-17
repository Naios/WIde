
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import javafx.beans.property.ObjectProperty;

import com.github.naios.wide.core.framework.storage.server.helper.StructureState;

public interface ServerStoragePrivateBase
{
    public void setOwner(final ServerStorage<?> owner);

    public ObjectProperty<StructureState> writeableState();
}
