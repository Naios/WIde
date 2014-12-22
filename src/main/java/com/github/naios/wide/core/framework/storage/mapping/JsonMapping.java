
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.core.framework.util.CrossIterator;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.Pair;
import com.github.naios.wide.core.framework.util.StringUtil;

public class JsonMapping<FROM, TO extends Mapping<BASE>, BASE> implements Mapping<BASE>
{
    private final MappingPlan plan;

    private final List<Pair<BASE, MappingMetaData>> values;

    private final List<Pair<BASE, MappingMetaData>> keys;

    private final List<Object> rawHashableKeys;

    public JsonMapping(final MapperBase<FROM, TO, BASE> mapper, final MappingPlan plan,
            final List<Pair<BASE, MappingMetaData>> values)
    {
        this.plan = plan;

        this.values = Collections.unmodifiableList(values);

        final List<Pair<BASE, MappingMetaData>> keys = new ArrayList<>();
        final List<Object> rawHashableKeys = new ArrayList<>();

        values.forEach((entry) ->
        {
            Object realValue;
            try
            {
                realValue = mapper.getAdapterOf(
                                plan.getMappedTypes().get(
                                        plan.getOrdinalOfName(
                                                entry.second().getName())))
                                    .getRawHashableValue(entry.first());
            }
            catch (final Exception e)
            {
                // Should never happen
                throw new Error(e);
            }

            if (entry.second().isKey())
            {
                keys.add(entry);
                rawHashableKeys.add(realValue);
            }
        });

        this.keys = Collections.unmodifiableList(keys);
        this.rawHashableKeys = Collections.unmodifiableList(rawHashableKeys);
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
    public List<Object> getHashableKeys()
    {
        return rawHashableKeys;
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
            if (other.getHashableKeys() != null)
                return false;
        }
        else if (!rawHashableKeys.equals(other.getHashableKeys()))
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return getHashableKeys().hashCode();
    }

    @Override
    public String toString()
    {
        return "{" + Arrays.toString(getHashableKeys().toArray()) + " -> " +
                    StringUtil.concat(", ",
                            new CrossIterator<>(this, entry->
                                entry.second().getName() + " = " +
                                    new FormatterWrapper(entry.first()).toString())) + "}";
    }
}
