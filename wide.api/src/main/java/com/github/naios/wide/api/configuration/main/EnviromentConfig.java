
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.configuration.main;

import java.util.List;

import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.entities.GameBuild;

public interface EnviromentConfig
{
    /**
     * @return Name of the enviroment
     */
    public StringProperty name();

    /**
     * @return Path to the alias definition
     */
    public StringProperty aliasDefinition();

    /**
     * @return The {@link ClientStorageConfig}
     */
    public ClientStorageConfig getClientStorageConfig();

    /**
     * @return The {@link GameBuild} of the enviroment
     */
    public GameBuild getBuild();

    /**
     * @return The {@link DatabaseConfig}'s of the enviroment
     */
    public List<DatabaseConfig> getDatabases();

    /**
     * @return The {@link DatabaseConfig} with a specific id
     */
    public DatabaseConfig getDatabaseConfig(String id);
}
