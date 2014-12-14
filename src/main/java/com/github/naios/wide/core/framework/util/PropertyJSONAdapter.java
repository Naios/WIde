
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.value.WritableValue;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PropertyJSONAdapter<T extends WritableValue<?>> implements JsonSerializer<T>, JsonDeserializer<T>, InstanceCreator<T>
{
    private final BiConsumer<T, JsonElement> set;

    private final Function<T, JsonElement> get;

    private final Supplier<T> create;

    public PropertyJSONAdapter(final BiConsumer<T, JsonElement> set, final Function<T, JsonElement> get, final Supplier<T> create)
    {
        this.set = set;
        this.get = get;
        this.create = create;
    }

    @Override
    public JsonElement serialize(final T src, final Type type,
            final JsonSerializationContext context)
    {
        return get.apply(src);
    }

    @Override
    public T deserialize(final JsonElement json, final Type type,
            final JsonDeserializationContext context) throws JsonParseException
    {
        final T obj = create.get();
        set.accept(obj, json);
        return obj;
    }

    @Override
    public T createInstance(final Type type)
    {
        return create.get();
    }
}
