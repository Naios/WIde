
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import javafx.beans.property.BooleanProperty;

import com.github.naios.wide.configuration.VariablizeConfig;

public class VariablizeConfigImpl implements VariablizeConfig
{
    private BooleanProperty custom, names, enums, flags;

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
}
