
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import com.github.naios.wide.api.util.StringUtil;

class SQLVariable implements Comparable<SQLVariable>
{
    private final String name, value, prefix;

    private static final int ORDER_DEFAULT = 0;

    private final int order;

    public SQLVariable(final String name, final String value)
    {
        this.name = StringUtil.convertStringToVarName(name);
        this.value = value;

        int order;
        try
        {
            order = StringUtil.convertToInt(value);
        }
        catch (final Throwable t)
        {
            order = ORDER_DEFAULT;
        }

        this.order = order;

        final int prefixIdx = name.indexOf(SQLVariableHolder.PREFIX_DELIMITER);

        if (prefixIdx == -1)
            this.prefix = SQLVariableHolder.PREFIX_NONE;
        else
            this.prefix = name.substring(0, prefixIdx);
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public int compareTo(final SQLVariable other)
    {
        final int comp = prefix.compareTo(other.prefix);
        if (comp != 0)
            return comp;

        return Integer.compare(order, other.order);
    }
}
