
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.framework.internal.storage.mapping.MappingImplementation;
import com.github.naios.wide.framework.internal.storage.server.helper.StructureState;

public class ServerStorageBaseImplementation
    implements ServerStoragePrivateBase, MappingImplementation<ServerStorageStructure>
{
    private final ObjectProperty<StructureState> state =
            new SimpleObjectProperty<>(StructureState.STATE_UNKNOWN);

    private ServerStorageStructure structure;

    @Override
    public void callback(final ServerStorageStructure structure)
    {
        this.structure = structure;
    }

    private ServerStorage<?> owner;

    @Override
    public ServerStorage<?> getOwner()
    {
        return owner;
    }

    @Override
    public void setOwner(final ServerStorage<?> owner)
    {
        this.owner = owner;
    }

    @Override
    public ReadOnlyObjectProperty<StructureState> state()
    {
        return state;
    }

    @Override
    public ObjectProperty<StructureState> writeableState()
    {
        return state;
    }

    @Override
    public void delete()
    {
        getOwner().onStructureDeleted(structure);
    }

    @Override
    public void reset()
    {
        getOwner().onStructureReset(structure);
    }
}
