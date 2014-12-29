
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.api.configuration.main.QueryConfig;
import com.github.naios.wide.api.configuration.main.QueryTypeConfig;
import com.github.naios.wide.configuration.internal.util.Saveable;

public class QueryConfigImpl implements QueryConfig, Saveable
{
    private BooleanProperty compress = new SimpleBooleanProperty(false);

    private List<QueryTypeConfigImpl> type = new ArrayList<>();

    @Override
    public BooleanProperty compress()
    {
        return compress;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<QueryTypeConfig> getType()
    {
        return (List)type;
    }

    @Override
    public void save()
    {
        type.forEach(t -> t.save());
    }
}
