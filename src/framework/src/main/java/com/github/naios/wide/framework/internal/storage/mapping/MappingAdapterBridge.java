
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

/**
 * Helps us to reduce raw class hacks to a minimum
 */
public class MappingAdapterBridge<FROM, TO extends Mapping<BASE>, BASE>
    implements MappingAdapterBase<FROM, TO, BASE, BASE, Object>
{
    private final MappingAdapter<FROM, TO, BASE, ? extends BASE, ?> adapter;

    public MappingAdapterBridge(final MappingAdapter<FROM, TO, BASE, ? extends BASE, ?> adapter)
    {
        this.adapter = adapter;
    }

    @Override
    public TypeToken<?> getType()
    {
        return adapter.getType();
    }

    @Override
    public TypeToken<?> getPrimitive()
    {
        return adapter.getPrimitive();
    }

    @Override
    public BASE map(final FROM from, final TO to, final MappingPlan<BASE> plan, final int index,
            final MappingMetaData metaData)
    {
        return adapter.map(from, to, plan, index, metaData);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public BASE create(final TO to, final MappingPlan<BASE> plan, final int index,
            final MappingMetaData metaData, final Optional<Object> value)
    {
     // TODO Check this
        return adapter.create(to, plan, index, metaData, (Optional)value);
    }

    @Override
    public boolean isPossibleKey()
    {
        return adapter.isPossibleKey();
    }

    @Override
    public Object getRawHashableValue(final BASE me)
    {
        return adapter.getRawHashableValue(me);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean set(final BASE me, final Object value)
    {
        // TODO Check this
        return ((MappingAdapter)adapter).set(me, value);
    }

    @Override
    public boolean setDefault(final BASE me)
    {
        return adapter.setDefault(me);
    }
}
