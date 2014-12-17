
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.Pair;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE> extends MapperBase<FROM, TO, BASE>
{
    private final MappingPlan plan;

    public JsonMapper(final TableSchema schema, final Class<? extends TO> target,
            final Class<?> implementation)
    {
        this(schema, new MappingAdapterHolder<>(), target, implementation);
    }

    public JsonMapper(final TableSchema schema, final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final Class<? extends TO> target, final Class<?> implementation)
    {
        super(adapterHolder, target, implementation);
        this.plan = new JsonMappingPlan(schema, getTarget(), getImplementation());
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

    @Override
    protected Mapping<BASE> newMappingBasedOn(final List<Object> keys)
    {
        // TODO
        return null;
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
