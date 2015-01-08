
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.config.main.QueryType;
import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.config.internal.ConfigHolder;

public class QueryConfigImpl implements QueryConfig
{
    private BooleanProperty compress = new SimpleBooleanProperty(false);

    private Map<QueryType, QueryTypeConfigImpl> types =
            new HashMap<QueryType, QueryTypeConfigImpl>();

    @Override
    public BooleanProperty compress()
    {
        return compress;
    }

    @Override
    public QueryTypeConfig getConfigForType(final QueryType type)
    {
        QueryTypeConfigImpl config = types.get(type);
        if (Objects.isNull(config))
        {
            config = new QueryTypeConfigImpl();
            types.put(type, config);
        }

        return config;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Entry<QueryType, QueryTypeConfig>> getQueryTypeConfigs()
    {
        return (Set)types.entrySet();
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
