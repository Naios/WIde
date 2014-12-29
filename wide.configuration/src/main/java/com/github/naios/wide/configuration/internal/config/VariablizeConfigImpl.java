
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.api.configuration.main.VariablizeConfig;
import com.github.naios.wide.configuration.internal.util.Saveable;

public class VariablizeConfigImpl implements VariablizeConfig, Saveable
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
    public void save()
    {
    }
}
