
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import java.util.List;

import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.framework.game.GameBuild;

public class EnviromentConfig
{
    private StringProperty name, client_storages;

    public GameBuild build;

    private List<DatabaseConfig> databases;

    public StringProperty name()
    {
        return name;
    }

    public StringProperty client_storages()
    {
        return client_storages;
    }

    public List<DatabaseConfig> getDatabases()
    {
        return databases;
    }
}
