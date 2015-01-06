
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

import java.util.Map.Entry;
import java.util.Set;

public interface Schema
{
    public String getName();

    public String getDescription();

    public Set<Entry<String, TableSchema>> getTables();

    public TableSchema getSchemaOf(String name);
}
