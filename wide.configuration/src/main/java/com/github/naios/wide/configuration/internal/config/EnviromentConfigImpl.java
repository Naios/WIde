
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.entities.GameBuild;

@SuppressWarnings("serial")
class MissingDatabaseConfig extends RuntimeException
{
    public MissingDatabaseConfig(final String name)
    {
        super(String.format("Requested database (%s) is not present in the config!", name));
    }
}

public class EnviromentConfigImpl
{
    private final StringProperty name;

    private final GameBuild build;

    private final StringProperty alias_definition;

    private final ClientStorageConfig client_storages;

    private final List<DatabaseConfigImpl> databases;

    public EnviromentConfigImpl(final GameBuild build, final StringProperty alias_definition,
            final ClientStorageConfig client_storages)
    {
        this.name = new SimpleStringProperty();
        this.alias_definition = new SimpleStringProperty();
        this.build = build;
        this.client_storages = client_storages;
        this.databases = new ArrayList<>();
    }

    public StringProperty name()
    {
        return name;
    }

    public StringProperty alias_definition()
    {
        return alias_definition;
    }

    public ClientStorageConfig getClientStorageConfig()
    {
        return client_storages;
    }

    public GameBuild getBuild()
    {
        return build;
    }

    public List<DatabaseConfigImpl> getDatabases()
    {
        return databases;
    }

    public boolean isDatabasePresent(final String id)
    {
        for (final DatabaseConfigImpl db : databases)
            if (db.id().get().equals(id))
                return true;

        return false;
    }

    public DatabaseConfigImpl getDatabaseConfig(final String id)
    {
        for (final DatabaseConfigImpl db : databases)
            if (db.id().get().equals(id))
                return db;

        throw new MissingDatabaseConfig(id);
    }
}
