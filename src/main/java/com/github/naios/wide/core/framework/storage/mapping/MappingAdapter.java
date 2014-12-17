
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

public abstract class MappingAdapter<FROM, BASE>
{
    public abstract BASE map(FROM from, MappingPlan plan, int index, MappingMetaData metaData);

    /**
     * @param value is null if the default value needs to be set
     */
    public abstract BASE create(MappingPlan plan, int index, MappingMetaData metaData, Object value);

    public BASE createHelper(final BASE me, final Object value)
    {
        if (value != null)
            set(me, value);
        else
            setDefault(me);

        return me;
    }

    public boolean isPossibleKey()
    {
        return false;
    }

    /**
     * If you use the type as key, return its real value for hashing
     */
    public Object getRawHashableValue(final BASE me)
    {
        return me;
    }

    public boolean set(final BASE me, final Object value)
    {
        return false;
    }

    public boolean setDefault(final BASE me)
    {
        return false;
    }
}
