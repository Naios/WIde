
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.github.naios.wide.api.framework.storage.mapping.UnknownMappingEntryException;
import com.github.naios.wide.api.util.CrossIterator;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.api.util.StringUtil;
import com.google.common.reflect.TypeToken;

public class JsonMapping<FROM, TO extends Mapping<BASE>, BASE> implements Mapping<BASE>
{
    private final MapperBase<FROM, TO, BASE> mapper;

    private final MappingPlan<BASE> plan;

    private final List<Pair<BASE, MappingMetaData>> values;

    private final List<Pair<BASE, MappingMetaData>> keys;

    private final List<Object> rawHashableKeys;

    public JsonMapping(final MapperBase<FROM, TO, BASE> mapper, final MappingPlan<BASE> plan,
            final List<Pair<BASE, MappingMetaData>> values)
    {
        this.mapper = mapper;

        this.plan = plan;

        this.values = Collections.unmodifiableList(values);

        final List<Pair<BASE, MappingMetaData>> keys = new ArrayList<>();
        final List<Object> rawHashableKeys = new ArrayList<>();

        values.forEach((entry) ->
        {
            if (entry.second().isKey())
            {
                keys.add(entry);
                rawHashableKeys.add(getRawValue(entry));
            }
        });

        this.keys = Collections.unmodifiableList(keys);
        this.rawHashableKeys = Collections.unmodifiableList(rawHashableKeys);
    }

    private Object getRawValue(final Pair<BASE, MappingMetaData> entry)
    {
        try
        {
            final TypeToken<? extends BASE> typeToken =
                    plan.getMappedTypes().get(
                            plan.getOrdinalOfName(entry.second().getName()));

            return mapper.getAdapterOf(typeToken).getPrimitiveValue(entry.first());
        }
        catch (final Exception e)
        {
            // Should never happen
            throw new Error(e);
        }
    }

    @Override
    public Iterator<Pair<BASE, MappingMetaData>> iterator()
    {
        return values.iterator();
    }

    @Override
    public List<Pair<BASE, MappingMetaData>> getKeys()
    {
        return keys;
    }

    @Override
    public List<Object> getRawKeys()
    {
        return rawHashableKeys;
    }

    @Override
    public List<Object> getRawValues()
    {
        final List<Object> hashableValues = new ArrayList<>(values.size());
        forEach(entry -> hashableValues.add(getRawValue(entry)));
        return hashableValues;
    }

    @Override
    public List<Pair<BASE, MappingMetaData>> getValues()
    {
        return values;
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

    @Override
    public Pair<BASE, MappingMetaData> getEntryByTarget(final String name)
            throws UnknownMappingEntryException
    {
        try
        {
            return values.get(plan.getOrdinalOfTarget(name));
        }
        catch (final OrdinalNotFoundException e)
        {
            throw new UnknownMappingEntryException(name);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (!(obj instanceof Mapping))
            return false;

        final Mapping other = (Mapping) obj;
        if (rawHashableKeys == null)
        {
            if (other.getRawKeys() != null)
                return false;
        }
        else if (!rawHashableKeys.equals(other.getRawKeys()))
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return getRawKeys().hashCode();
    }

    @Override
    public String toString()
    {
        return "{" + Arrays.toString(getRawKeys().toArray()) + " -> " +
                    StringUtil.concat(", ",
                            new CrossIterator<>(this, entry->
                                entry.second().getName() + " = " +
                                    new FormatterWrapper(entry.first()).toString())) + "}";
    }
}
