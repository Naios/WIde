/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration;

import com.github.naios.wide.configuration.internal.config.QueryType;
import com.github.naios.wide.configuration.internal.config.VariablizeConfigImpl;

public interface QueryTypeConfig
{
    /**
     * @return The {@link QueryType} of this entry
     */
    public QueryType getType();

    /**
     * @return The {@link VariablizeConfigImpl} of this entry
     */
    public VariablizeConfigImpl getVariablize();
}
