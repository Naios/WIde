
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.entities.NoSucheEntityException;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE> extends MapperBase<FROM, TO, BASE>
{
    private final MappingPlan plan;

    @SuppressWarnings("rawtypes")
    public JsonMapper(final TableSchema schema, final List<Class<?>> interfaces,
                final Class<? extends MappingImplementation> implementation)
    {
        this(schema, new MappingAdapterHolder<>(), interfaces, implementation);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public JsonMapper(final TableSchema schema, final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final List<Class<?>> interfaces,
                final Class<? extends MappingImplementation> implementation)
    {
        super(adapterHolder, getTargetOfSchema(schema), interfaces, implementation);
        this.plan = new JsonMappingPlan(schema, getTarget(), getImplementation());
    }

    @SuppressWarnings("rawtypes")
    private static Class getTargetOfSchema(final TableSchema schema)
    {
        try
        {
            return FrameworkServiceImpl.getEntityService().requestClass(schema.getStructure());
        }
        catch (final NoSucheEntityException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public MappingPlan getPlan()
    {
        return plan;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Mapping<BASE> newMappingBasedOn(final FROM from)
    {
        final List<Pair<BASE, MappingMetaData>> content =
                new ArrayList<>();

        for (int i = 0; i < plan.getNumberOfElements(); ++i)
        {
            final MappingAdapter adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            content.add(new Pair(adapter.map(from, plan, i, plan.getMetadata().get(i)), plan.getMetadata().get(i)));
        }

        return new JsonMapping<>(this, plan, content);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Mapping<BASE> newMappingBasedOn(final List<Object> keys)
    {
        final List<Pair<BASE, MappingMetaData>> content =
                new ArrayList<>();

        final Iterator<Object> iterator = keys.iterator();
        for (int i = 0; i < plan.getNumberOfElements(); ++i)
        {
            final MappingAdapter<FROM, BASE> adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            final MappingMetaData metaData = plan.getMetadata().get(i);
            final BASE base = adapter.create(plan, i, metaData, metaData.isKey() ? iterator.next() : null);

            content.add(new Pair(base, plan.getMetadata().get(i)));
        }

        return new JsonMapping<>(this, plan, content);
    }

    @Override
    public boolean set(final String name, final BASE base, final Object value)
    {
        MappingAdapter<FROM, BASE> adapter;
        try
        {
            adapter = getAdapterOf(plan.getMappedTypes().get(plan.getOrdinalOfName(name)));
        }
        catch (final OrdinalNotFoundException e)
        {
            throw new Error(e);
        }

        return adapter.set(base, value);
    }

    @Override
    public boolean reset(final String name, final BASE base)
    {
        MappingAdapter<FROM, BASE> adapter;
        try
        {
            adapter = getAdapterOf(plan.getMappedTypes().get(plan.getOrdinalOfName(name)));
        }
        catch (final OrdinalNotFoundException e)
        {
            throw new Error(e);
        }

        return adapter.setDefault(base);
    }
}
