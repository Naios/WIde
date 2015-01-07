
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.alias;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.config.alias.AliasType;
import com.github.naios.wide.config.internal.ConfigServiceImpl;

public class AliasImpl implements Alias
{
    private AliasType type;

    private StringProperty database = new SimpleStringProperty(""),
            target = new SimpleStringProperty(""),
                    entryColumn = new SimpleStringProperty(""),
                        nameColumn = new SimpleStringProperty("");

    private IntegerProperty entryColumnIndex = new SimpleIntegerProperty(0),
        nameColumnIndex = new SimpleIntegerProperty(0);

    private StringProperty prefix = new SimpleStringProperty(""),
            zeroName = new SimpleStringProperty(""),
                    failPrefix = new SimpleStringProperty("");

    @Override
    public AliasType getAliasType()
    {
        return type;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends Enum> getTarget()
    {
        return ConfigServiceImpl.getEntityService().requestEnum(target.get());
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
    public StringProperty zeroName()
    {
        return zeroName;
    }

    @Override
    public StringProperty failPrefix()
    {
        return failPrefix;
    }
}
