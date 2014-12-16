
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.core.framework.util.Pair;

public class JsonMappingImplementation<FROM, TO extends Mapping<BASE>, BASE> implements Mapping<BASE>
{
    private final JsonMapper<FROM, TO, BASE> mapper;

    private final List<Pair<? extends BASE, MappingMetadata>> content;

    public JsonMappingImplementation(final JsonMapper<FROM, TO, BASE> mapper,
            final List<Pair<? extends BASE, MappingMetadata>> content)
    {
        this.mapper = mapper;

        this.content = content;
    }

    @Override
    public Iterator<Pair<? extends BASE, MappingMetadata>> iterator()
    {
        return content.iterator();
    }

    @Override
    public List<Pair<BASE, MappingMetadata>> getKeys()
    {
        return null;
    }

    @Override
    public List<Pair<BASE, MappingMetadata>> getValues()
    {
        return null;
    }

    @Override
    public void setDefaultValues()
    {
    }

    @Override
    public Pair<? extends BASE, MappingMetadata> getEntryByName(final String name)
    {
        return content.get(mapper.getOrdinalOfName(name));
    }
}
