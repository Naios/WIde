
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

public class QueryTypeConfig
{
    private final QueryType type;

    private final VariablizeConfig variablize;

    public QueryTypeConfig(final QueryType type, final VariablizeConfig variablize)
    {
        this.type = type;
        this.variablize = variablize;
    }

    public QueryType getType()
    {
        return type;
    }

    public VariablizeConfig getVariablize()
    {
        return variablize;
    }
}
