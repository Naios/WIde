
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.List;

import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.google.common.reflect.TypeToken;

public interface Mapper<FROM, TO extends Mapping<BASE>, BASE>
{
    public TO map(FROM from);

    public TO createEmpty(List<Object> keys);

    public boolean set(String name, BASE base, Object value);

    public boolean reset(String name, BASE base);

    @SuppressWarnings("rawtypes")
    public Mapper<FROM, TO, BASE> registerAdapter(final TypeToken type,
            final MappingAdapter<FROM, ? extends BASE> adapter);

    public MappingPlan getPlan();
}
