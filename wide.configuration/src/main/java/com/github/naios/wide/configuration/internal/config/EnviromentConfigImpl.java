
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import java.util.List;

import javafx.beans.property.StringProperty;

import com.github.naios.wide.configuration.DatabaseConfig;
import com.github.naios.wide.configuration.EnviromentConfig;
import com.github.naios.wide.entities.GameBuild;

@SuppressWarnings("serial")
class MissingDatabaseConfig extends RuntimeException
{
    public MissingDatabaseConfig(final String name)
    {
        super(String.format("Requested database (%s) is not present in the config!", name));
    }
}

public class EnviromentConfigImpl implements EnviromentConfig
{
    private StringProperty name;

    private GameBuild build;

    private StringProperty alias_definition;

    private ClientStorageConfigImpl client_storages;

    private List<DatabaseConfigImpl> databases;

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
}
