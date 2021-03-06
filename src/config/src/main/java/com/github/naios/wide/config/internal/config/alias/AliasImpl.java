
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.alias;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.config.alias.AliasType;
import com.github.naios.wide.config.internal.ConfigHolder;

public class AliasImpl implements Alias
{
    private AliasType type = AliasType.ENUM;

    private StringProperty database = new SimpleStringProperty(""),
            target = new SimpleStringProperty(""),
                    entryColumn = new SimpleStringProperty(""),
                        nameColumn = new SimpleStringProperty("");

    private IntegerProperty entryColumnIndex = new SimpleIntegerProperty(0),
        nameColumnIndex = new SimpleIntegerProperty(0);

    private StringProperty prefix = new SimpleStringProperty(""),
                    failPrefix = new SimpleStringProperty("");

    private Map<Integer, String> custom = new HashMap<>();

    @Override
    public AliasType getAliasType()
    {
        return type;
    }

    @Override
    public StringProperty target()
    {
        return target;
    }

    @Override
    public StringProperty database()
    {
        return database;
    }

    @Override
    public StringProperty entryColumn()
    {
        return entryColumn;
    }

    @Override
    public IntegerProperty entryColumnIndex()
    {
        return entryColumnIndex;
    }

    @Override
    public StringProperty nameColumn()
    {
        return nameColumn;
    }

    @Override
    public IntegerProperty nameColumnIndex()
    {
        return nameColumnIndex;
    }

    @Override
    public StringProperty prefix()
    {
        return prefix;
    }

    @Override
    public Map<Integer, String> customEntries()
    {
        return custom;
    }

    @Override
    public StringProperty failPrefix()
    {
        return failPrefix;
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
