
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.util;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LazyGsonAdapter<T>
    implements JsonSerializer<T>, JsonDeserializer<T>, InstanceCreator<T>
{
    private final Function<JsonElement, T> set;

    private final Function<T, JsonElement> get;

    private final Supplier<T> create;

    public LazyGsonAdapter(final Function<JsonElement, T> set,
            final Function<T, JsonElement> get, final Supplier<T> create)
    {
        this.set = set;
        this.get = get;
        this.create = create;
    }

    @Override
    public JsonElement serialize(final T src, final Type type,
            final JsonSerializationContext context)
    {
        System.out.println(String.format("Serializing: %s (%s) - %s", src, src.getClass().getName(), context));
        return get.apply(src);
    }

    @Override
    public T deserialize(final JsonElement json, final Type type,
            final JsonDeserializationContext context) throws JsonParseException
    {
        return set.apply(json);
    }

    @Override
    public T createInstance(final Type type)
    {
        return create.get();
    }
}
