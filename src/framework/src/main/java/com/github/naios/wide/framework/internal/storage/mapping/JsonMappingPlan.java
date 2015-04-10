
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.framework.storage.mapping.MappingPlan;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;

public class JsonMappingPlan<BASE extends ReadOnlyProperty<?>> implements MappingPlan<BASE>
{
    private final BiMap<String, Integer> nameToOrdinal =
            HashBiMap.create();

    private final BiMap<String, Integer> targetToOrdinal =
            HashBiMap.create();

    private final List<MappingMetaData> data;

    private final List<TypeToken<? extends BASE>> mappedType;

    private final List<MappingMetaData> keys;

    @SuppressWarnings("unchecked")
    public JsonMappingPlan(final TableSchema schema, final Class<?> target,
            final Function<MappingMetaData, Optional<TypeToken<?>>> typeReceiver, final Class<?> implementation)
    {
        // Calculate the plan based on the schema and the target
        // Methods defined in target must be defined in the schema.
        final List<MappingMetaData> data = new ArrayList<>();
        final List<MappingMetaData> keys = new ArrayList<>();
        final List<TypeToken<? extends BASE>> mappedTypes = new ArrayList<>();

        final Collection<Method> methods = new HashSet<>(Arrays.asList(target.getMethods()));
        methods.removeAll(Arrays.asList(ServerStorageStructure.class.getMethods()));

        int i = 0;
        for (final MappingMetaData metaData : schema.getEntries())
        {
            final Method method = getMethodByName(methods, metaData.getTarget());
            final TypeToken<?> type;
            if (method == null)
            {
                type = typeReceiver.apply(metaData)
                        .orElseThrow(() -> new RuntimeException("Could not get type of schema entry " + metaData));
            }
            else
            {
                type = TypeToken.of(method.getReturnType());
                methods.remove(method);
            }

            mappedTypes.add((TypeToken<? extends BASE>) type);

            data.add(metaData);
            nameToOrdinal.put(metaData.getName(), i);
            targetToOrdinal.put(metaData.getTarget(), i);

            if (metaData.isKey())
                keys.add(metaData);

            ++i;
        }

        if (!methods.isEmpty())
            throw new RuntimeException(String.format("Structure fields %s are not present in the schema of \"%s\", " +
                    "interface mapped fields must be written in schematics! Target is \"%s\".",
                        methods.stream().map(Method::getName).map(s -> String.format("\"%s\"", s)).collect(Collectors.joining(", ")), schema.getName(), target));

        // Check if all keys are present in the interface
        i = 0;
        for (final MappingMetaData metaData : schema.getEntries())
            if (metaData.isKey())
                ++i;

        if (i != keys.size())
            throw new RuntimeException(String.format("Interface %s defines not all keys presented in schema %s.", target, schema.getName()));

        this.data = Collections.unmodifiableList(data);
        this.keys = Collections.unmodifiableList(keys);
        this.mappedType = Collections.unmodifiableList(mappedTypes);
    }

    public Method getMethodByName(final Collection<Method> methods, final String name)
    {
        for (final Method method : methods)
            if (method.getName().equals(name))
            {
                methods.remove(method);
                return method;
            }

        return null;
    }

    @Override
    public int getNumberOfElements()
    {
        return data.size();
    }

    @Override
    public List<MappingMetaData> getKeys()
    {
        return keys;
    }

    @Override
    public int getNumberOfKeys()
    {
        return keys.size();
    }

    @Override
    public List<MappingMetaData> getMetaData()
    {
        return data;
    }

    @Override
    public String getNameOfOrdinal(final int ordinal)
    {
        return nameToOrdinal.inverse().get(ordinal);
    }

    @Override
    public int getOrdinalOfName(final String name) throws OrdinalNotFoundException
    {
        if (nameToOrdinal.containsKey(name))
            return nameToOrdinal.get(name);
        else
            throw new OrdinalNotFoundException(name);
    }

    @Override
    public int getOrdinalOfTarget(final String name) throws OrdinalNotFoundException
    {
        if (targetToOrdinal.containsKey(name))
            return targetToOrdinal.get(name);
        else
            throw new OrdinalNotFoundException(name);
    }

    @Override
    public List<TypeToken<? extends BASE>> getMappedTypes()
    {
        return mappedType;
    }
}
