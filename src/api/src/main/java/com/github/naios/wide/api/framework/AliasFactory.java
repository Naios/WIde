
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework;

import java.util.Map;

// TODO convert this into a service
public interface AliasFactory
{
    /**
     * Returns the name for a specific alias value
     * @param name  The unique name of the alias defined in "alias.json"
     * @param value The value you want to translate into the alias
     * @return      Returns the alias for a specific value
     */
    public String requestAlias(String name, int value);

    /**
     * Returns a map containing all names of an alias
     * @param name  The unique name of the alias defined in "alias.json"
     * @return     Returns the alias map
     */
    public Map<Integer, String> requestAllAliases(String name);

    /**
     * Reloads all aliases
     */
    public void reloadAliases();
}
