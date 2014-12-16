
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import com.google.common.reflect.TypeToken;

public abstract class MapperBase<FROM, TO extends Mapping<?>> implements Mapper<FROM, TO>
{
    @Override
    public Mapper<FROM, TO> registerAdapter(final TypeToken<?> type, final MappingAdapter adapter)
    {
        return this;
    }
}
