
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.Optional;

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

    public final TypeToken<ADAPTED_TYPE> getType()
    {
        return type;
    }

    protected abstract Object getMappedValue(FROM from, TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData);

    public final ADAPTED_TYPE map(final FROM from, final TO to, final MappingPlan<BASE> plan, final int index, final MappingMetaData metaData)
    {
        return create(to, plan, index, metaData, Optional.of(getMappedValue(from, to, plan, index, metaData)));
    }

    /**
     * @param value value is not present then the default value is set
     */
    public abstract ADAPTED_TYPE create(TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData, Optional<Object> value);

    /**
     * Sets the value to the type if present, otherwise set the default value
     *
     * @param me    The ADAPTED_TYPE
     * @param value The Value you want to set
     * @return      Returns the ADAPTED_TYPE (me param)
     */
    public final ADAPTED_TYPE setValueOrDefaultIfNotPresent(final ADAPTED_TYPE me, final Optional<Object> value)
    {
        if (value.isPresent())
            setAdaptedType(me, value);
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

    protected boolean setAdaptedType(final ADAPTED_TYPE me, final Object value)
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final boolean set(final BASE me, final Object value)
    {
        if (type.isAssignableFrom(TypeToken.of(me.getClass())))
            throw new IllegalArgumentException(type + " is not assignable from " + me.getClass());

        return setAdaptedType((ADAPTED_TYPE) me, value);
    }

    protected abstract Object getDefault();

    public final boolean setDefault(final BASE me)
    {
        return set(me, getDefault());
    }
}
