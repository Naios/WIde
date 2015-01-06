
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.alias;

import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.property.StringProperty;

public interface AliasConfig
{
    public StringProperty name();

    public StringProperty description();

    public StringProperty version();

    public Alias getAliasForName(String name);

    public Set<Entry<String, Alias>> getAliases();
}
