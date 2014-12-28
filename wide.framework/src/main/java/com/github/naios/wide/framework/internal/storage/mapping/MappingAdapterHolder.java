
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
class MissingMappingAdapterException extends RuntimeException
{
   public MissingMappingAdapterException(final String name)
   {
       super(String.format("Missing MappingAdapter for class %s, did you forgot to register it?", name));
   }
}

public class MappingAdapterHolder<FROM, TO extends Mapping<BASE>, BASE>
{
    private final Map<TypeToken<? extends BASE>,  MappingAdapter<FROM, ? extends BASE>> adapter =
            new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MappingAdapterHolder<FROM, TO, BASE> registerAdapter(final TypeToken type,
            final MappingAdapter<FROM, ? extends BASE> adapter)
    {
        this.adapter.put(type, adapter);
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected MappingAdapter<FROM, BASE> getAdapterOf(final TypeToken<? extends BASE> type)
    {
        final MappingAdapter adapter = this.adapter.get(type);
        if (adapter != null)
            return adapter;
        else
            throw new MissingMappingAdapterException(type.toString());
    }
}
