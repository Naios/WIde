
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureState;
import com.github.naios.wide.framework.internal.storage.mapping.MappingImplementation;

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

    private ServerStorageImpl<?> owner;

    @Override
    public ServerStorageImpl<?> getOwner()
    {
        return owner;
    }

    @Override
    public void setOwner(final ServerStorageImpl<?> owner)
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
