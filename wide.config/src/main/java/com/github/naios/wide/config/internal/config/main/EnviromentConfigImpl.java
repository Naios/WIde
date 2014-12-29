
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.DatabaseConfig;
import com.github.naios.wide.api.config.main.EnviromentConfig;
import com.github.naios.wide.api.entities.GameBuild;
import com.github.naios.wide.config.internal.util.ConfigHolder;
import com.github.naios.wide.config.internal.util.Saveable;

@SuppressWarnings("serial")
class MissingDatabaseConfig extends RuntimeException
{
    public MissingDatabaseConfig(final String name)
    {
        super(String.format("Requested database (%s) is not present in the config!", name));
    }
}

public class EnviromentConfigImpl implements EnviromentConfig, Saveable
{
    private StringProperty name = new SimpleStringProperty("");

    private GameBuild build = GameBuild.DEFAULT_BUILD;

    private StringProperty alias_definition = new SimpleStringProperty("");

    private ClientStorageConfigImpl client_storages;

    private List<DatabaseConfigImpl> databases = new ArrayList<>();

    @Override
    public StringProperty name()
    {
        return name;
    }

    @Override
    public StringProperty aliasDefinition()
    {
        return alias_definition;
    }

    @Override
    public ClientStorageConfigImpl getClientStorageConfig()
    {
        return client_storages;
    }

    @Override
    public GameBuild getBuild()
    {
        return build;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<DatabaseConfig> getDatabases()
    {
        return (List)databases;
    }

    private boolean isDatabasePresent(final String id)
    {
        for (final DatabaseConfigImpl db : databases)
            if (db.id().get().equals(id))
                return true;

        return false;
    }

    @Override
    public DatabaseConfigImpl getDatabaseConfig(final String id)
    {
        for (final DatabaseConfigImpl db : databases)
            if (db.id().get().equals(id))
                return db;

        throw new MissingDatabaseConfig(id);
    }

    @Override
    public void save()
    {
        client_storages.save();
        databases.forEach(d -> d.save());
    }

    @Override
    public String toString()
    {
        return ConfigHolder.getJsonOfObject(this);
    }
}
