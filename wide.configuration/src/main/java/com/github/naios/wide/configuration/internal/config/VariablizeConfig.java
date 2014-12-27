
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class VariablizeConfig
{
    private final BooleanProperty custom, names, enums, flags;

    public VariablizeConfig()
    {
        this.custom = new SimpleBooleanProperty();
        this.names = new SimpleBooleanProperty();
        this.enums = new SimpleBooleanProperty();
        this.flags = new SimpleBooleanProperty();
    }

    public BooleanProperty custom()
    {
        return custom;
    }

    public BooleanProperty names()
    {
        return names;
    }

    public BooleanProperty enums()
    {
        return enums;
    }

    public BooleanProperty flags()
    {
        return flags;
    }
}
