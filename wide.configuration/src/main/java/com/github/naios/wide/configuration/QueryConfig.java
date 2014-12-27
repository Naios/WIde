/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration;

import java.util.List;

import com.github.naios.wide.configuration.internal.config.QueryTypeConfigImpl;

import javafx.beans.property.BooleanProperty;

public interface QueryConfig
{

    public BooleanProperty compress();

    public List<QueryTypeConfigImpl> getType();

}
