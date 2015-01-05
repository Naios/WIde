
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.DatabaseConfig;
import com.github.naios.wide.api.config.main.EnviromentConfig;
import com.github.naios.wide.api.entities.GameBuild;
import com.github.naios.wide.config.internal.util.ConfigHolder;

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
    private GameBuild build = GameBuild.DEFAULT_BUILD;

    private StringProperty alias_definition = new SimpleStringProperty("");

    private ClientStorageConfigImpl client_storages;

    @SuppressWarnings("serial")
    private Map<String, DatabaseConfigImpl> databases =
            new HashMap<String, DatabaseConfigImpl>();

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
    public Set<Entry<String, DatabaseConfig>> getDatabases()
    {
        return (Set)databases.entrySet();
    }

    @Override
    public DatabaseConfig getDatabaseConfig(final String id)
    {
        final DatabaseConfig db = databases.get(id);
        if (Objects.nonNull(db))
            return db;
        else
            throw new MissingDatabaseConfig(id);
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
