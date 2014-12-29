
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.main;

import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

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
     * @return All Enviroments as list
     */
    public List<EnviromentConfig> getEnviroments();

    /**
     * @return Our query config
     */
    public QueryConfig getQueryConfig();

    /**
     * @return The current active enviroment
     */
    public EnviromentConfig getActiveEnviroment();
}
