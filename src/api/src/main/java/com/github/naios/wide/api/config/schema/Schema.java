
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

public interface Schema
{
    public ReadOnlyStringProperty name();

    public ReadOnlyStringProperty description();

    public StringProperty version();

    public Set<Entry<String, TableSchema>> getTables();

    public Optional<TableSchema> getSchemaOf(String name);
}
