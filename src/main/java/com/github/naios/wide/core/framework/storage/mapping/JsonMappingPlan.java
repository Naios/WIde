
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;

public class JsonMappingPlan implements MappingPlan
{
    private final BiMap<Integer, String> ordinalToName =
            HashBiMap.create();

    private final List<MappingMetadata> data;

    private final List<TypeToken<?>> mappedType;

    private final List<Integer> keys;

    public JsonMappingPlan(final TableSchema schema, final Class<?> target, final Class<?> implementation)
    {
        // Calculate the plan based on the schema and the target
        // Methods defined in target must be defined in the schema.
        // Non-key Fields defined in the schema must not presented in the target interface
        final List<MappingMetadata> data = new ArrayList<>();
        final List<Integer> keys = new ArrayList<>();
        final List<TypeToken<?>> mappedType = new ArrayList<>();

        // Get methods that are not covered through the implementations
        final List<Method> methods = new ArrayList<>();
        methods.addAll(Arrays.asList(target.getMethods()));

        methods.removeIf(first ->
        {
            for (final Method second : implementation.getMethods())
                if (methodSignatureEquals(first, second))
                    return true;

            return false;
        });

        methods.removeIf(first ->
        {
            for (final Method second : JsonMapping.class.getMethods())
                if (methodSignatureEquals(first, second))
                    return true;

            return false;
        });

        int i = 0;
        for (final Method method : methods)
        {
            final MappingMetadata metaData = getMetaDataInListByName(schema.getEntries(), method.getName());
            if (metaData == null)
                throw new RuntimeException(String.format("Structure field %s is not present in the schema!", method.getName()));

            mappedType.add(TypeToken.of(method.getReturnType()));
            data.add(metaData);
            ordinalToName.put(i++, method.getName());

            if (metaData.isKey())
                keys.add(i);
        }

        // Check if all keys are present in the interface
        i = 0;
        for (final MappingMetadata metaData : schema.getEntries())
            if (metaData.isKey())
                ++i;

        if (i != keys.size())
            throw new RuntimeException(String.format("Interface %s defines not all keys present in schema %s.", target, schema.getName()));

        this.data = Collections.unmodifiableList(data);
        this.keys = Collections.unmodifiableList(keys);
        this.mappedType = Collections.unmodifiableList(mappedType);
    }

    // TODO Fix this dirty workaround
    private boolean methodSignatureEquals(final Method first, final Method second)
    {
        if (first == second)
            return true;

        if (first == null ||
            second == null)
            return false;

        if (!first.getName().equals(second.getName()))
            return false;

        if (!Arrays.equals(first.getParameterTypes(), second.getParameterTypes()))
            return false;

        if (!first.getReturnType().equals(second.getReturnType()))
            return false;

        return true;
    }

    private MappingMetadata getMetaDataInListByName(final List<MappingMetadata> metaData, final String name)
    {
        for (final MappingMetadata data : metaData)
            if (data.getName().equals(name))
                return data;

        return null;
    }

    @Override
    public int getNumberOfElements()
    {
        return data.size();
    }

    @Override
    public List<Integer> getKeys()
    {
        return keys;
    }

    @Override
    public int getNumberOfKeys()
    {
        return keys.size();
    }

    @Override
    public List<MappingMetadata> getMetadata()
    {
        return data;
    }

    @Override
    public String getNameOfOrdinal(final int ordinal)
    {
        return ordinalToName.get(ordinal);
    }

    @Override
    public int getOrdinalOfName(final String name) throws OrdinalNotFoundException
    {
        if (ordinalToName.containsValue(name))
            return ordinalToName.inverse().get(name);
        else
            throw new OrdinalNotFoundException(name);
    }

    @Override
    public List<TypeToken<?>> getMappedType()
    {
        return mappedType;
    }
}
