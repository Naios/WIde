
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration;

import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.configuration.internal.config.EnviromentConfigImpl;
import com.github.naios.wide.configuration.internal.config.QueryConfigImpl;

public interface Config
{
    /**
     * @return The title of the config
     */
    public ReadOnlyStringProperty title();

    /**
     * @return The description of the config
     */
    public ReadOnlyStringProperty description();

    /**
     * @return The name of our current active config
     */
    public StringProperty activeEnviroment();

    /**
     * @return All Enviroments
     */
    public List<EnviromentConfigImpl> getEnviroments();

    /**
     * @return Our query config
     */
    public QueryConfigImpl getQueryConfig();

    /**
     * @return The current active enviroment
     */
    public EnviromentConfigImpl getActiveEnviroment();
}
