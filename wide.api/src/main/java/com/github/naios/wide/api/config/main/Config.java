
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.main;

import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

public interface Config
{
    /**
     * Any UI is called on start.
     */
    public static final String DEFAULT_UI_AUTO = "any";

    /**
     * No UI  is called on start.
     */
    public static final String DEFAULT_UI_SKIP = "skip";

    /**
     * @return The title of the config
     */
    public ReadOnlyStringProperty title();

    /**
     * @return The description of the config
     */
    public ReadOnlyStringProperty description();

    /**
     * @return The WIde license
     */
    public ReadOnlyStringProperty license();

    /**
     * @return The preferred User Interface
     */
    public StringProperty ui();

    /**
     * @return The name of our current active config
     */
    public StringProperty activeEnviroment();

    /**
     * @return All Enviroments as entry set
     */
    public Set<Entry<String, EnviromentConfig>> getEnviroments();

    /**
     * @return Our query config
     */
    public QueryConfig getQueryConfig();

    /**
     * @return The current active enviroment
     */
    public EnviromentConfig getActiveEnviroment();

    /**
     * @return Boolean property that says if we want to compress the config<br>
     *         (Don't save default values).
     */
    public BooleanProperty compress();
}
