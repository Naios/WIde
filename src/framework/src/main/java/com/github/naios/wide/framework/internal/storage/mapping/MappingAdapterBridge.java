
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.Optional;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.MappingPlan;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.reflect.TypeToken;

/**
 * Helps us to reduce raw class hacks to a minimum
 */
public class MappingAdapterBridge<FROM, TO extends Mapping<BASE>, BASE extends ReadOnlyProperty<?>>
    implements MappingAdapterBase<FROM, TO, BASE, BASE, Object>
{
    @SuppressWarnings("rawtypes")
    private final MappingAdapter adapter;

    public MappingAdapterBridge(final MappingAdapter<FROM, TO, BASE, ? extends BASE, ?> adapter)
    {
        this.adapter = adapter;
    }

    final static TypeToken<?> wrapper = TypeToken.of(Wrapper.class);

    private void tokenInstanceOf(final TypeToken<?> type, final Object object, final boolean isPrimitive)
    {
        final TypeToken<?> typeToken = TypeToken.of(object.getClass());
        if (!type.isAssignableFrom(typeToken))
            throw new IllegalArgumentException("Adapted Type " + type + " is not assignable from " + typeToken);

        /*
        if (isPrimitive && !typeToken.isPrimitive() && !type.isAssignableFrom(wrapper))
            throw new IllegalArgumentException(typeToken + " is not a primitive type!");*/
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

    @SuppressWarnings("unchecked")
    @Override
    public BASE map(final FROM from, final TO to, final MappingPlan<BASE> plan, final int index,
            final MappingMetaData metaData)
    {
        return (BASE) adapter.map(from, to, plan, index, metaData);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BASE create(final TO to, final MappingPlan<BASE> plan, final int index,
            final MappingMetaData metaData, final Optional<Object> value)
    {
        value.ifPresent(object -> tokenInstanceOf(adapter.getPrimitive(), object, true));
        return (BASE) adapter.create(to, plan, index, metaData, value);
    }

    @Override
    public boolean isPossibleKey()
    {
        return adapter.isPossibleKey();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getPrimitiveValue(final BASE me)
    {
        tokenInstanceOf(adapter.getType(), me, false);
        return adapter.getPrimitiveValue(me);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean set(final BASE me, final Object value)
    {
        tokenInstanceOf(adapter.getType(), me, false);
        tokenInstanceOf(adapter.getPrimitive(), value, true);
        return adapter.set(me, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean setDefault(final BASE me)
    {
        tokenInstanceOf(adapter.getType(), me, false);
        return adapter.setDefault(me);
    }
}
