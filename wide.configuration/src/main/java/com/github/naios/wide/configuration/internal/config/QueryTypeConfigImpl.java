
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.configuration.internal.util.Saveable;

public class QueryTypeConfigImpl implements QueryTypeConfig, Saveable
{
    private QueryType type = QueryType.UNDEFINED;

    private VariablizeConfigImpl variablize;

    @Override
    public QueryType getType()
    {
        return type;
    }

    @Override
    public VariablizeConfigImpl getVariablize()
    {
        return variablize;
    }

    @Override
    public void save()
    {
        variablize.save();
    }
}
