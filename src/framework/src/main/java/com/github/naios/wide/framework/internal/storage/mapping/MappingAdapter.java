
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.Objects;
import java.util.Optional;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.google.common.reflect.TypeToken;

public abstract class MappingAdapter<FROM, TO extends Mapping<BASE>, BASE, ADAPTED_TYPE extends BASE, PRIMITIVE>
    implements MappingAdapterBase<FROM, TO, BASE, ADAPTED_TYPE, PRIMITIVE>
{
    private TypeToken<ADAPTED_TYPE> type;

    private TypeToken<PRIMITIVE> primitive;

    public MappingAdapter(final Class<ADAPTED_TYPE> type, final Class<PRIMITIVE> primitive)
    {
       this(TypeToken.of(type), TypeToken.of(primitive));

       Objects.requireNonNull(type);
       Objects.requireNonNull(primitive);
    }

    public MappingAdapter(final TypeToken<ADAPTED_TYPE> type, final TypeToken<PRIMITIVE> primitive)
    {
        this.type = type;
        this.primitive = primitive;
    }

    @Override
    public final TypeToken<ADAPTED_TYPE> getType()
    {
        return type;
    }

    @Override
    public final TypeToken<PRIMITIVE> getPrimitive()
    {
        return primitive;
    }

    protected abstract PRIMITIVE getDefault();

    protected abstract PRIMITIVE getMappedValue(FROM from, TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData);

    @Override
    public final ADAPTED_TYPE map(final FROM from, final TO to, final MappingPlan<BASE> plan, final int index, final MappingMetaData metaData)
    {
        return create(to, plan, index, metaData, Optional.ofNullable(getMappedValue(from, to, plan, index, metaData)));
    }

    /**
     * @param value value is not present then the default value is set
     */
    @Override
    public abstract ADAPTED_TYPE create(TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData, Optional<PRIMITIVE> value);

    /**
     * If you use the type as key, return its real value for hashing
     */
    @Override
    public Object getRawHashableValue(final ADAPTED_TYPE me)
    {
        return me;
    }

    // Overwrite this to allow modification of mapped types
    protected boolean setAdaptedType(final ADAPTED_TYPE me, final PRIMITIVE value)
    {
        return false;
    }

    @Override
    public final boolean set(final ADAPTED_TYPE me, final PRIMITIVE value)
    {
        return setAdaptedType(me, value);
    }

    @Override
    public boolean setDefault(final ADAPTED_TYPE me)
    {
        return set(me, getDefault());
    }
}
