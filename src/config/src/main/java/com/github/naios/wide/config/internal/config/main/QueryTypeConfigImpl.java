
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.config.internal.ConfigHolder;

public class QueryTypeConfigImpl implements QueryTypeConfig
{
    private BooleanProperty custom = new SimpleBooleanProperty(false), names = new SimpleBooleanProperty(false),
            enums = new SimpleBooleanProperty(false), flags = new SimpleBooleanProperty(false);

    @Override
    public BooleanProperty custom()
    {
        return custom;
    }

    @Override
    public BooleanProperty names()
    {
        return names;
    }

    @Override
    public BooleanProperty enums()
    {
        return enums;
    }

    @Override
    public BooleanProperty flags()
    {
        return flags;
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
