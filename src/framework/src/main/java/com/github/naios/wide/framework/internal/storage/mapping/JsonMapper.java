
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
    private final MappingPlan<BASE> plan;

    public JsonMapper(final TableSchema schema, final List<Class<?>> interfaces,
                final Class<? extends MappingCallback<?>> implementation)
    {
        this(schema, new MappingAdapterHolder<>(), interfaces, implementation);
    }


    public JsonMapper(final TableSchema schema, final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final List<Class<?>> interfaces,
                final Class<? extends MappingCallback<?>> implementation)
    {
        super(adapterHolder, getTargetOfSchema(schema), interfaces, implementation);
        this.plan = new JsonMappingPlan<>(schema, getTarget(), getImplementation());
    }

    @SuppressWarnings({ "unchecked" })
    private static <TO> Class<? extends TO> getTargetOfSchema(final TableSchema schema)
    {
        try
        {
            return (Class<? extends TO>) FrameworkServiceImpl.getEntityService().requestClass(schema.getStructure());
        }
        catch (final NoSucheEntityException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public MappingPlan<BASE> getPlan()
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
            final MappingAdapter<FROM, TO, BASE, ? extends BASE> adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            content.add(new Pair(adapter.map(from, /*FIXME*/ null, plan, i, plan.getMetadata().get(i)), plan.getMetadata().get(i)));
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
            final MappingAdapter<FROM, TO, BASE, ? extends BASE> adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            final MappingMetaData metaData = plan.getMetadata().get(i);
            final BASE base = adapter.create(/*FIXME*/ null, plan, i, metaData, metaData.isKey() ? iterator.next() : null);

            content.add(new Pair(base, plan.getMetadata().get(i)));
        }

        return new JsonMapping<>(this, plan, content);
    }

    @Override
    public boolean set(final String name, final BASE base, final Object value)
    {
        MappingAdapter<FROM, TO, BASE, ? extends BASE> adapter;
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
        MappingAdapter<FROM, TO, BASE, ?> adapter;
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
