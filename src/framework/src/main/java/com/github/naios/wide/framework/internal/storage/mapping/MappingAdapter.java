
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.google.common.reflect.TypeToken;

public abstract class MappingAdapter<FROM, TO extends Mapping<BASE>, BASE, ADAPTED_TYPE extends BASE>
{
    private TypeToken<ADAPTED_TYPE> type;

    public MappingAdapter(final Class<ADAPTED_TYPE> type)
    {
        this.type = TypeToken.of(type);
    }

    public MappingAdapter(final TypeToken<ADAPTED_TYPE> type)
    {
        this.type = type;
    }

    public TypeToken<ADAPTED_TYPE> getType()
    {
        return type;
    }

    public abstract ADAPTED_TYPE map(FROM from, TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData);

    /**
     * @param value is null if the default value needs to be set
     */
    public abstract ADAPTED_TYPE create(TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData, Object value);

    public final ADAPTED_TYPE createHelper(final ADAPTED_TYPE me, final Object value)
    {
        if (value != null)
            setOverwrite(me, value);
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

    protected boolean setOverwrite(final ADAPTED_TYPE me, final Object value)
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final boolean set(final BASE me, final Object value)
    {
        if (type.isAssignableFrom(TypeToken.of(me.getClass())))
            throw new IllegalArgumentException(type + " is not assignable from " + me.getClass());

        return setOverwrite((ADAPTED_TYPE) me, value);
    }

    public boolean setDefault(final BASE me)
    {
        return false;
    }
}
