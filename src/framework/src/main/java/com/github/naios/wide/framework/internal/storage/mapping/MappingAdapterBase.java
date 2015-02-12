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

public interface MappingAdapterBase<FROM, TO extends Mapping<BASE>, BASE, ADAPTED_TYPE extends BASE, PRIMITIVE>
{
    public TypeToken<?> getType();

    public TypeToken<?> getPrimitive();

    public ADAPTED_TYPE map(final FROM from, final TO to, final MappingPlan<BASE> plan, final int index, final MappingMetaData metaData);

    public ADAPTED_TYPE create(TO to, MappingPlan<BASE> plan, int index, MappingMetaData metaData, Optional<PRIMITIVE> value);

    public default boolean isPossibleKey()
    {
        return false;
    }

    public Object getRawHashableValue(final ADAPTED_TYPE me);

    public default boolean set(final ADAPTED_TYPE me, final PRIMITIVE value)
    {
        return false;
    }

    public default boolean setDefault(final ADAPTED_TYPE me)
    {
        return false;
    }
}
