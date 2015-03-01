
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.github.naios.wide.api.framework.storage.mapping.UnknownMappingEntryException;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.google.common.reflect.TypeToken;

public class JsonMapping<FROM, TO extends Mapping<BASE>, BASE extends ReadOnlyProperty<?>> implements Mapping<BASE>
{
    private final MapperBase<FROM, TO, BASE> mapper;

    private final MappingPlan<BASE> plan;

    private final List<BASE> values;

    private final List<BASE> keys;

    private final List<Object> rawHashableKeys;

    public JsonMapping(final MapperBase<FROM, TO, BASE> mapper, final MappingPlan<BASE> plan, final List<BASE> values)
    {
        this.mapper = mapper;

        this.plan = plan;

        this.values = Collections.unmodifiableList(values);

        final List<BASE> keys = new ArrayList<>();
        final List<Object> rawHashableKeys = new ArrayList<>();

        values.forEach(entry ->
        {
            if (MappingBeans.getMetaData(entry).isKey())
            {
                keys.add(entry);
                rawHashableKeys.add(getRawValue(entry));
            }
        });

        this.keys = Collections.unmodifiableList(keys);
        this.rawHashableKeys = Collections.unmodifiableList(rawHashableKeys);
    }

    private Object getRawValue(final BASE property)
    {
        try
        {
            final TypeToken<? extends BASE> typeToken =
                    plan.getMappedTypes().get(
                            plan.getOrdinalOfName(MappingBeans.getMetaData(property).getName()));

            return mapper.getAdapterOf(typeToken).getPrimitiveValue(property);
        }
        catch (final Exception e)
        {
            // Should never happen
            throw new Error(e);
        }
    }

    @Override
    public Iterator<BASE> iterator()
    {
        return values.iterator();
    }

    @Override
    public List<BASE> getKeys()
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
    public List<BASE> getValues()
    {
        return values;
    }

    @Override
    public BASE getEntryByName(final String name)
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
    public BASE getEntryByTarget(final String name)
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
        return "{" + getRawKeys().stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]")) + " -> " +
                stream()
                    .map(property-> MappingBeans.getMetaData(property).getName() + " = " + new FormatterWrapper(property).toString())
                    .collect(Collectors.joining(", "))
                   + "}";
    }
}
