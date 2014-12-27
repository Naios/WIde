
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.util;

import java.lang.reflect.Modifier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

public class GsonInstance
{
    /**
     * The {@link Gson} instance used in this bundle<br>
     * Including registered type adapters for javafx propertys and pretty print set
     */
    public final static Gson INSTANCE = new GsonBuilder()
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
        .create();

    /**
     * Converts an object to json<br>
     * Deletes default value declarations such as int=0, boolean=false or empty strings
     */
    public static String toJsonExcludeDefaultValues(final Object obj)
    {
        return INSTANCE.toJson(obj)
                .replaceAll(" *\".*\": (0|false|\"\"),\n", "")
                .replaceAll(",\n *\".*\": (0|false|\"\")", "");
    }
}
