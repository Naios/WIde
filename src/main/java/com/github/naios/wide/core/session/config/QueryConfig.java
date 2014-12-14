
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import java.util.List;

import javafx.beans.property.BooleanProperty;

public class QueryConfig
{
    private BooleanProperty compress;

    private List<QueryTypeConfig> type;

    public BooleanProperty compress()
    {
        return compress;
    }

    public List<QueryTypeConfig> getType()
    {
        return type;
    }
}
