
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public class ChangeTrackerImpl
    implements ChangeTracker
{
    private final FrameworkWorkspace workspace;

    private final static String DEFAULT_SCOPE = "";

    private final static String DEFAULT_SCOPE_COMMENT = "";

    private final StringProperty scope =
            new SimpleStringProperty(DEFAULT_SCOPE);

    private final Map<String, String> scopeComments =
            new HashMap<>();

    private final Map<ServerStorageStructure, String> structureScopes =
            new HashMap<>();

    private final Map<StructureEntryStorageIndex, String> entryScopes =
            new HashMap<>();

    private final Map<ServerStorageStructure, Map<ReadOnlyProperty<?>, String>> customVariables = new HashMap<>();

    private final ObservableSet<ServerStorageStructure> created = FXCollections.observableSet();

    private final ObservableSet<ServerStorageStructure> deleted = FXCollections.observableSet();

    private final ObservableSet<SQLUpdateInfo> updated = FXCollections.observableSet();

    public ChangeTrackerImpl(final FrameworkWorkspace workspace)
    {
        this.workspace = workspace;
    }

    public void onCreate(final ServerStorageStructure structure)
    {
        deleted.remove(created);
        created.add(structure);

        if (hasScopeSet())
            structureScopes.put(structure, scope.get());
    }

    public void onDelete(final ServerStorageStructure structure)
    {
        created.remove(created);
        deleted.add(structure);

        if (hasScopeSet())
            structureScopes.put(structure, scope.get());
    }

    public void onUpdate(final ServerStorageStructure structure,
            final ReadOnlyProperty<?> property, final Object oldValue)
    {
        if (!created.contains(structure))
        {
            final SQLUpdateInfo info = new SQLUpdateInfoImpl(property, oldValue);
            if (updated.contains(info));
                updated.add(info);
        }
    }

    @Override
    public ObservableSet<ServerStorageStructure> structuresCreated()
    {
        return created;
    }

    @Override
    public ObservableSet<ServerStorageStructure> structuresDeleted()
    {
        return deleted;
    }

    @Override
    public ObservableSet<SQLUpdateInfo> propertiesUpdated()
    {
        return updated;
    }

    @Override
    public String getScopeOfEntry(final ServerStorageStructure structure,
            final ReadOnlyProperty<?> property)
    {
        final String scope = entryScopes.get(new StructureEntryStorageIndex(property));
        if (Objects.nonNull(scope))
            return scope;
        else
            return getScopeOfStructure(structure);
    }

    @Override
    public String getScopeOfStructure(final ServerStorageStructure structure)
    {
        return structureScopes.get(structure);
    }

    @Override
    public StringProperty scope()
    {
        return scope;
    }

    @Override
    public boolean hasScopeSet()
    {
        return !scope.get().equals(DEFAULT_SCOPE);
    }

    @Override
    public void setScope(final String scope)
    {
        this.scope.set(scope);
    }

    @Override
    public void setScope(final String scope, final String comment)
    {
        scopeComments.put(scope, comment);
        setScope(scope);
    }

    @Override
    public void releaseScope()
    {
        scope.set(DEFAULT_SCOPE);
    }

    @Override
    public String getCommentOfScope(final String scope)
    {
        return scopeComments.getOrDefault(scope, DEFAULT_SCOPE_COMMENT);
    }

    /**
     * Sets the comment of the current scope
     * @param comment the comment you want to set
     */
    @Override
    public void setScopeComment(final String comment)
    {
        scopeComments.put(scope.get(), comment);
    }

    @Override
    public String getCustomVariable(final ServerStorageStructure structure, final ReadOnlyProperty<?> observable)
    {
        final Map<ReadOnlyProperty<?>, String> map = customVariables.get(structure);
        return Objects.isNull(map) ? null : map.get(observable);
    }

    @Override
    public void setCustomVariable(final ServerStorageStructure structure, final ReadOnlyProperty<?> observable, final String name)
    {
        Map<ReadOnlyProperty<?>, String> map = customVariables.get(structure);
        if (Objects.isNull(map))
        {
            map = new IdentityHashMap<>();
            customVariables.put(structure, map);
        }

        map.put(observable, name);
    }

    @Override
    public void releaseCustomVariable(final ServerStorageStructure structure, final ReadOnlyProperty<?> observable)
    {
        final Map<ReadOnlyProperty<?>, String> map = customVariables.get(structure);
        if (Objects.isNull(map))
            return;

        map.remove(observable);

        if (map.isEmpty())
            customVariables.remove(map);
    }

    @Override
    public void commit()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public String getQuery()
    {
        return workspace.createSQLBuilder(this).toString();
    }

    @Override
    public String toString()
    {
        return String.format("Updated  : %s\nInserted : %s\nUpdated  : %s\nStructure Scopes  : %s\nEntry Scopes  : %s\nScope Comments  : %s",
                updated, created, deleted, structureScopes, entryScopes, scopeComments);
    }
}
