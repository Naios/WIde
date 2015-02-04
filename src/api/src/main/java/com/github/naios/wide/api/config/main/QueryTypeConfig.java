
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.main;

import javafx.beans.property.BooleanProperty;

public interface QueryTypeConfig
{
    public BooleanProperty custom();

    public BooleanProperty alias();

    public BooleanProperty enums();

    public BooleanProperty flags();
}
