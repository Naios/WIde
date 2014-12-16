
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

public interface MappingAdapter<FROM, BASE>
{
    public BASE map(FROM from, MappingPlan plan, int index, MappingMetaData metaData);

    public default boolean isPossibleKey()
    {
        return false;
    }

    /**
     * If you use the type as key, return its real value for hashing
     */
    public default Object getRealValue(final BASE me)
    {
        return me;
    }

    public default boolean set(final BASE me, final Object value)
    {
        return false;
    }

    public default boolean setDefault(final BASE me)
    {
        return false;
    }
}
