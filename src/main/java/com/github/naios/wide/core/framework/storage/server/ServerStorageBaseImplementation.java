
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.core.framework.storage.server.helper.StructureState;

public class ServerStorageBaseImplementation implements ServerStorageBase
{
    private final ObjectProperty<StructureState> state =
            new SimpleObjectProperty<>(StructureState.STATE_UNKNOWN);

    private ServerStorage<?> owner;

    @Override
    public ServerStorage<?> getOwner()
    {
        return owner;
    }

    protected void setOwner(final ServerStorage<?> owner)
    {
        this.owner = owner;
    }

    @Override
    public ReadOnlyObjectProperty<StructureState> state()
    {
        return state;
    }

    protected ObjectProperty<StructureState> writeableState()
    {
        return state;
    }

    @Override
    public void delete()
    {
        // TODO
    }

    @Override
    public void reset()
    {
        // TODO
    }
}
