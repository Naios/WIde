
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.util;

import com.google.gson.Gson;

public class GsonInstance
{
    public final static Gson INSTANCE = null;
    /*new GsonBuilder()
        // Pretty print
        .setPrettyPrinting()
        // Exclude static fields
        .excludeFieldsWithModifiers(Modifier.STATIC)
        // StringProperty Adapter
        .registerTypeAdapter(StringProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleStringProperty(json.getAsJsonPrimitive().getAsString()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleStringProperty()))
        // IntegerProperty Adapter
        .registerTypeAdapter(IntegerProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleIntegerProperty(json.getAsJsonPrimitive().getAsInt()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleIntegerProperty()))
        // FloatProperty Adapter
        .registerTypeAdapter(FloatProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleFloatProperty(json.getAsJsonPrimitive().getAsFloat()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleFloatProperty()))
        // BooleanProperty Adapter
        .registerTypeAdapter(BooleanProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleBooleanProperty(json.getAsJsonPrimitive().getAsBoolean()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleBooleanProperty()))
        .create();*/

    public static String toJsonExcludeDefaultValues(final Object obj)
    {
        return INSTANCE.toJson(obj)
                // Delete default value declaration (minify .json)
                .replaceAll(" *\".*\": (0|false|\"\"),\n", "")
                .replaceAll(",\n *\".*\": (0|false|\"\")", "");
    }
}
