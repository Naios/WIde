/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.framework.storage.mapping.MappingPlan;

/**
 * The {@link ServerStorage} class which exists once per database table
 * provides database rows as structures.
 */
public interface ServerStorage<T extends ServerStorageStructure>
{
    /**
     * @return Returns the name
     */
    public String getTableName();

    /**
     * @return Returns the internal database if of the structure.
     */
    public String getDatabaseId();

    /**
     * @return Returns a property which identifies if the database is still alive.
     */
    public ReadOnlyBooleanProperty alive();

    /**
     * Returns an optional {@link ServerStorageStructure}
     * @param key The key of the structure
     * @return The structure that mateches the key
     */
    public Optional<T> request(ServerStorageKey<T> key);

    /**
     * @return Returns multiple {@link ServerStorageStructure} which match the given SQL statement.
     */
    public List<T> requestWhere(String where, Object... args);

    /**
     * @return Returns multiple {@link ServerStorageStructure} which match the given SQL statement.
     */
    public List<T> requestWhere(String where);

    /**
     * @return Returns a new {@link ServerStorageStructure}
     */
    public T create(ServerStorageKey<T> key);

    /**
     * @return Returns the mapping plan for this {@link ServerStorage}
     */
    public MappingPlan<ReadOnlyProperty<?>> getMappingPlan();

    /**
     * @return Returns the {@link ChangeTracker} which is responsible for tracking this storage changes.
     */
    public ChangeTracker getChangeTracker();
}
