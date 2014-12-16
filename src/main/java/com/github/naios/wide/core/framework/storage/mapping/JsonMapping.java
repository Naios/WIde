
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
    private final MapperBase<FROM, TO, BASE> mapper;

    private final MappingPlan plan;

    private final List<Pair<BASE, MappingMetaData>> values;

    private final List<Pair<Object, MappingMetaData>> keys;

    private final List<Object> keyObjects;

    public JsonMapping(final MapperBase<FROM, TO, BASE> mapper, final MappingPlan plan,
            final List<Pair<BASE, MappingMetaData>> values)
    {
        this.mapper = mapper;

        this.plan = plan;

        this.values = Collections.unmodifiableList(values);

        final List<Pair<Object, MappingMetaData>> keys = new ArrayList<>();
        values.forEach((entry) ->
        {
            Object realValue;
            try
            {
                realValue = mapper.getAdapterOf(plan.getMappedType().get(plan.getOrdinalOfName(entry.second().getName())))
                        .getRealValue(entry.first());
            }
            catch (final Exception e)
            {
                // Should never happen
                throw new Error(e);
            }

            if (entry.second().isKey())
                keys.add(new Pair<>(realValue, entry.second()));
        });

        this.keys = Collections.unmodifiableList(keys);

        final List<Object> keyObjects = new ArrayList<>();
        this.keys.forEach(key -> keyObjects.add(key.first()));

        this.keyObjects = Collections.unmodifiableList(keyObjects);
    }

    @Override
    public Iterator<Pair<BASE, MappingMetaData>> iterator()
    {
        return values.iterator();
    }

    @Override
    public List<Pair<Object, MappingMetaData>> getKeys()
    {
        return keys;
    }

    @Override
    public List<Object> getKeyObjects()
    {
        return null;
    }

    @Override
    public List<Pair<BASE, MappingMetaData>> getValues()
    {
        return values;
    }

    @Override
    public boolean setDefaultValues()
    {
        boolean success = true;
        for (int i  = 0; i < values.size(); ++i)
            if (!mapper.reset(plan.getNameOfOrdinal(i), values.get(i).first()))
                success = false;

        return success;
    }

    @Override
    public Pair<BASE, MappingMetaData> getEntryByName(final String name)
    {
        try
        {
            return values.get(plan.getOrdinalOfName(name));
        }
        catch (final OrdinalNotFoundException e)
        {
            throw new UnknownMappingEntryException(name);
        }
    }
}
