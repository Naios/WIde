
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.Pair;
import com.google.common.reflect.TypeToken;

public class JsonMapper<FROM, TO extends Mapping<BASE>, BASE> extends MapperBase<FROM, TO, BASE>
{
    private final JsonMappingPlan plan;

    public JsonMapper(final TableSchema schema, final Class<? extends TO> target,
            final Class<?> implementation)
    {
        this(new MappingAdapterHolder<>(), schema , target, implementation);
    }

    public JsonMapper(final MappingAdapterHolder<FROM, TO, BASE> adapterHolder,
            final TableSchema schema, final Class<? extends TO> target, final Class<?> implementation)
    {
        super(adapterHolder, target, implementation);
        this.plan = new JsonMappingPlan(schema, getTarget(), getImplementation());
    }

    @Override
    protected Mapping<BASE> newMappingBasedOn(final FROM from)
    {
        final List<Pair<? extends BASE, MappingMetadata>> content =
                new ArrayList<>();

        for (final Method method : getTarget().getMethods())
        {
            final int ordinal;
            try
            {
                ordinal = plan.getOrdinalOfName(method.getName());
            }
            catch (final OrdinalNotFoundException e)
            {
                continue;
            }

            final MappingAdapter<FROM, ? extends BASE> adapter =
                    getAdapterOf(TypeToken.of(method.getReturnType()));

            System.out.println(String.format("\tMethod: %s", method.getName()));
        }

        return new JsonMapping<>(plan, content);
    }
}
