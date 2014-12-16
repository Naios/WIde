
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.Pair;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE> extends MapperBase<FROM, TO, BASE>
{
    private final Map<String, Integer> nameToOrdinal =
            new HashMap<>();

    private final TableSchema schema;

    public JsonMapper(final TableSchema schema, final Class<? extends TO> target, final Class<?>[] interfaces, final Class<?> implementation)
    {
        this(schema, target, Arrays.asList(interfaces), implementation);
    }

    public JsonMapper(final TableSchema schema, final Class<? extends TO> target,
            final List<Class<?>> interfaces, final Class<?> implementation)
    {
        super(target, interfaces, implementation);
        this.schema = schema;
    }

    @Override
    protected Mapping<BASE> newMappingBasedOn(final FROM from)
    {
        final List<Pair<? extends BASE, MappingMetadata>> content =
                new ArrayList<>();

        testInsertList(content);

        return new JsonMappingImplementation<>(this, content);
    }

    public void testInsertList(final List<Pair<? extends BASE, MappingMetadata>> content)
    {

    }

    protected int getOrdinalOfName(final String name)
    {
        // throw new UnknownMappingEntryException(name)
        return 0;
    }
}
