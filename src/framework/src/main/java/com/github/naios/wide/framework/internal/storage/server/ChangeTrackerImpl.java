
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

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.github.naios.wide.framework.internal.storage.server.builder.SQLUpdateInfoImpl;

public class ChangeTrackerImpl
    implements ChangeTracker
{
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

    private final Map<ServerStorageStructure, Map<ObservableValue<?>, String>> customVariables = new HashMap<>();

    private final SetProperty<ServerStorageStructure> created = new SimpleSetProperty<>(FXCollections.observableSet()),
            deleted = new SimpleSetProperty<>(FXCollections.observableSet());

    class UpdateMap extends SimpleMapProperty<ServerStorageStructure, SetProperty<SQLUpdateInfo>>
    {
        public UpdateMap()
        {
            super (FXCollections.observableHashMap());
        }

        public void addUpdate(final ServerStorageStructure structure, final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
        {
            SetProperty<SQLUpdateInfo> set = get(structure);
            if (Objects.isNull(set))
            {
                set = new SimpleSetProperty<>(FXCollections.observableSet());
                put(structure, set);
            }

            set.add(new SQLUpdateInfoImpl(entry, oldValue));

            if (hasScopeSet())
                entryScopes.put(new StructureEntryStorageIndex(structure, entry), scope.get());
        }

        public void removeUpdate(final ServerStorageStructure structure, final Pair<ObservableValue<?>, MappingMetaData> entry)
        {
            final SetProperty<SQLUpdateInfo> set = get(structure);
            if (Objects.nonNull(set))
            {
                // Use reference equality here
                for (final SQLUpdateInfo info : set)
                    if (info.getEntry() == entry)
                        set.remove(info);

                if (set.isEmpty())
                    removeUpdates(structure);
            }
        }

        public void removeUpdates(final ServerStorageStructure structure)
        {
            final SetProperty<SQLUpdateInfo> set = get(structure);
            if (Objects.nonNull(set))
            {
                set.forEach(entry -> entryScopes.remove(new StructureEntryStorageIndex(structure, entry.getEntry())));
                remove(structure);
            }
        }
    }

    private final UpdateMap updates = new UpdateMap();

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
        updates.removeUpdates(structure);
        deleted.add(structure);

        if (hasScopeSet())
            structureScopes.put(structure, scope.get());
    }

    public void onUpdate(final ServerStorageStructure structure,
            final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
    {
        if (!created.contains(structure))
            updates.addUpdate(structure, entry, oldValue);
    }

    @Override
    public ReadOnlySetProperty<ServerStorageStructure> structuresCreated()
    {
        return created;
    }

    @Override
    public ReadOnlySetProperty<ServerStorageStructure> structuresDeleted()
    {
        return deleted;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ReadOnlyMapProperty<ServerStorageStructure, ReadOnlySetProperty<SQLUpdateInfo>> entriesChanged()
    {
        return (ReadOnlyMapProperty)updates;
    }

    /*
     * Planned but not supported yet!
     * @Override
     * public ReadOnlyMapProperty<ServerStorage<?>, ReadOnlyMapProperty<ServerStorageStructure, ReadOnlyListProperty<StructureChangeEvent>>> changeMap()
     * {
     *     // TODO Auto-generated method stub
     *     return null;
     * }
     */

    @Override
    public String getScopeOfEntry(final ServerStorageStructure structure,
            final Pair<ObservableValue<?>, MappingMetaData> entry)
    {
        final String scope = entryScopes.get(new StructureEntryStorageIndex(structure, entry));
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
    public String getCustomVariable(final ServerStorageStructure structure, final ObservableValue<?> observable)
    {
        final Map<ObservableValue<?>, String> map = customVariables.get(structure);
        return Objects.isNull(map) ? null : map.get(observable);
    }

    @Override
    public void setCustomVariable(final ServerStorageStructure structure, final ObservableValue<?> observable, final String name)
    {
        Map<ObservableValue<?>, String> map = customVariables.get(structure);
        if (Objects.isNull(map))
        {
            map = new IdentityHashMap<>();
            customVariables.put(structure, map);
        }

        map.put(observable, name);
    }

    @Override
    public void releaseCustomVariable(final ServerStorageStructure structure, final ObservableValue<?> observable)
    {
        final Map<ObservableValue<?>, String> map = customVariables.get(structure);
        if (Objects.isNull(map))
            return;

        map.remove(observable);

        if (map.isEmpty())
            customVariables.remove(map);
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
        return FrameworkServiceImpl.getInstance().createSQLBuilder(this).toString();
    }

    @Override
    public String toString()
    {
        return String.format("Updated  : %s\nInserted : %s\nUpdated  : %s\nStructure Scopes  : %s\nEntry Scopes  : %s\nScope Comments  : %s",
                updates, created, deleted, structureScopes, entryScopes, scopeComments);
    }
}
