
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureChangeEvent;
import com.github.naios.wide.api.util.Pair;

public class ChangeTrackerImpl
    implements ChangeTracker
{
    public void track(final ServerStorageStructure structure)
    {

    }

    public void untrack(final ServerStorageStructure structure)
    {

    }

    @Override
    public String getScopeOfEntry(final ServerStorageStructure structure,
            final Pair<ObservableValue<?>, MappingMetaData> entry)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getScopeOfStructure(final ServerStorageStructure structure)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCommentOfScope(final String scope)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCustomVariable(final ServerStorageStructure structure,
            final Pair<ObservableValue<?>, MappingMetaData> entry)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlySetProperty<ServerStorageStructure> structuresCreated()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlySetProperty<ServerStorageStructure> structuresDeleted()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlySetProperty<Pair<ObservableValue<?>, MappingMetaData>> entriesChanged()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyMapProperty<ServerStorage<?>, ReadOnlyMapProperty<ServerStorageStructure, ReadOnlyListProperty<StructureChangeEvent>>> changeMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StringProperty scope()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScope(final String scope)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setScope(final String scope, final String comment)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void releaseScope()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCustomVariable(final ObservableValue<?> value, final String name)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void releaseCustomVariable(final ObservableValue<?> value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setScopeComment(final String comment)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void commit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String getQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
