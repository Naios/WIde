
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config;

import com.github.naios.wide.api.config.main.Config;

public interface ConfigService extends Config
{
    /**
     * Reloads the config from file
     */
    public void reload();

    /**
     * Saves the config to file
     */
    public void close();
}
