
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.game.GameBuild;

public class EnviromentConfig
{
    private final StringProperty name;

    private final GameBuild build;

    private final ClientStorageConfig client_storages;

    private final List<DatabaseConfig> databases;

    public EnviromentConfig(final GameBuild build, final ClientStorageConfig client_storages)
    {
        this.name = new SimpleStringProperty();
        this.build = build;
        this.client_storages = client_storages;
        this.databases = new ArrayList<>();
    }

    public StringProperty name()
    {
        return name;
    }

    public ClientStorageConfig client_storages()
    {
        return client_storages;
    }

    public GameBuild getBuild()
    {
        return build;
    }

    public List<DatabaseConfig> getDatabases()
    {
        return databases;
    }
}
