
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
import java.util.Optional;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.entities.NoSucheEntityException;
import com.github.naios.wide.api.framework.storage.mapping.Mapping;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE extends ReadOnlyProperty<?>> extends MapperBase<FROM, TO, BASE>
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

    private static <TO> Class<? extends TO> getTargetOfSchema(final TableSchema schema)
    {
        try
        {
            return FrameworkServiceImpl.getEntityService().requestClass(schema.getStructure());
        }
        catch (final NoSucheEntityException e)
        {
            // TODO Throw malformed config maybe?
            throw new Error(e);
        }
    }

    @Override
    public MappingPlan<BASE> getPlan()
    {
        return plan;
    }

    @Override
    protected Mapping<BASE> newMappingBasedOn(final FROM from, final TO to)
    {
        final List<BASE> content = new ArrayList<>(plan.getNumberOfElements());

        for (int i = 0; i < plan.getNumberOfElements(); ++i)
        {
            final MappingAdapterBridge<FROM, TO, BASE> adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            content.add(adapter.map(from, to, plan, i, plan.getMetadata().get(i)));
        }

        return new JsonMapping<>(this, plan, content);
    }

    @Override
    protected Mapping<BASE> newMappingBasedOn(final List<Object> keys, final TO to)
    {
        final List<BASE> content = new ArrayList<>(plan.getNumberOfElements());

        final Iterator<Object> iterator = keys.iterator();
        for (int i = 0; i < plan.getNumberOfElements(); ++i)
        {
            final MappingAdapterBridge<FROM, TO, BASE> adapter =
                    getAdapterOf(plan.getMappedTypes().get(i));

            final MappingMetaData metaData = plan.getMetadata().get(i);
            final Optional<Object> value = Optional.ofNullable(metaData.isKey() ? iterator.next() : null);

            final BASE base = adapter.create(to, plan, i, metaData, value);
            content.add(base);
        }

        return new JsonMapping<>(this, plan, content);
    }

    @Override
    public boolean set(final String name, final BASE base, final Object value)
    {
        MappingAdapterBridge<FROM, TO, BASE> adapter;
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
        MappingAdapterBridge<FROM, TO, BASE> adapter;
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
