
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

import java.util.List;

public interface Schema
{
    public String getName();

    public String getDescription();

    public List<TableSchema> getTables();

    public TableSchema getSchemaOf(String name);
}
