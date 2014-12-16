
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.core.framework.util.Pair;

public class JsonMapping<FROM, TO extends Mapping<BASE>, BASE> implements Mapping<BASE>
{
    private final MappingPlan plan;

    private final List<Pair<? extends BASE, MappingMetadata>> values;

    private final List<Pair<? extends BASE, MappingMetadata>> keys;

    public JsonMapping(final MappingPlan plan,
            final List<Pair<? extends BASE, MappingMetadata>> values)
    {
        this.plan = plan;

        this.values = Collections.unmodifiableList(values);

        final List<Pair<? extends BASE, MappingMetadata>> keys = new ArrayList<>();
        values.forEach((entry) ->
        {
            if (entry.second().isKey())
                keys.add(entry);
        });

        this.keys = Collections.unmodifiableList(keys);
    }

    @Override
    public Iterator<Pair<? extends BASE, MappingMetadata>> iterator()
    {
        return values.iterator();
    }

    @Override
    public List<Pair<? extends BASE, MappingMetadata>> getKeys()
    {
        return keys;
    }

    @Override
    public List<Pair<? extends BASE, MappingMetadata>> getValues()
    {
        return values;
    }

    @Override
    public void setDefaultValues()
    {

    }

    @Override
    public Pair<? extends BASE, MappingMetadata> getEntryByName(final String name)
    {
        try
        {
            return values.get(plan.getOrdinalOfName(name));
        } catch (final OrdinalNotFoundException e)
        {
            throw new UnknownMappingEntryException(name);
        }
    }
}
