
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config;

import com.github.naios.wide.api.config.main.Config;
import com.google.common.annotations.Beta;

public interface ConfigService extends Config
{
    /**
     * Returns an external config (used by plugins)
     *
     * @param name name of the config
     * @param type class of the config
     * @return Returns the deserialized config object
     */
    @Beta
    public <T> T getExternalConfig(String path, Class<T> type, Class<?>... interfaces);

    /**
     * Reloads the config from file
     */
    public void reload();

    /**
     * Saves the config to file
     */
    public void close();
}
