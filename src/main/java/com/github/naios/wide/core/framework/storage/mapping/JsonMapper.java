
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.schema.Schema;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE> extends MapperBase<FROM, TO, BASE>
{
    private final Schema schema;

    public JsonMapper(final Schema schema, final Class<? extends TO> target, final Class<?>[] interfaces, final Class<?> implementation)
    {
        this(schema, target, Arrays.asList(interfaces), implementation);
    }

    public JsonMapper(final Schema schema, final Class<? extends TO> target,
            final List<Class<?>> interfaces, final Class<?> implementation)
    {
        super(target, interfaces, implementation);
        this.schema = schema;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TO map(final FROM from)
    {
        final MappingProxy proxy = new MappingProxy(newImplementation());

        return (TO) Proxy.newProxyInstance(getClass().getClassLoader(), getInterfacesAsArray(), proxy);
    }
}
