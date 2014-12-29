
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.framework.storage.client.ClientStorageFormatImpl;
import com.github.naios.wide.api.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

public class ConfigHolder<T extends Saveable> implements Saveable
{
    /**
     * The {@link Gson} instance used in this bundle<br>
     * Including registered type adapters for javafx propertys and pretty print set
     */
    private final static Gson INSTANCE = new GsonBuilder()
        // Pretty print
        .setPrettyPrinting()
        // Exclude static and final fields
        .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL)
        // StringProperty Adapter
        .registerTypeAdapter(StringProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleStringProperty(json.getAsJsonPrimitive().getAsString()),
                            (observable) -> new JsonPrimitive(observable.get())))
        // IntegerProperty Adapter
        .registerTypeAdapter(IntegerProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleIntegerProperty(json.getAsJsonPrimitive().getAsInt()),
                            (observable) -> new JsonPrimitive(observable.get())))
        // FloatProperty Adapter
        .registerTypeAdapter(FloatProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleFloatProperty(json.getAsJsonPrimitive().getAsFloat()),
                            (observable) -> new JsonPrimitive(observable.get())))
        // BooleanProperty Adapter
        .registerTypeAdapter(BooleanProperty.class,
                new LazyGsonAdapter<>(
                        (json) -> new SimpleBooleanProperty(json.getAsJsonPrimitive().getAsBoolean()),
                            (observable) -> new JsonPrimitive(observable.get())))
        // ClientStorageFormat Adapter
        .registerTypeAdapter(ClientStorageFormatImpl.class,
                new LazyGsonAdapter<>(
                        (json) -> new ClientStorageFormatImpl(json.getAsJsonPrimitive().getAsString()),
                            (format) -> new JsonPrimitive(format.getFormat())))
        .create();

    /**
     * Converts an object to json<br>
     * Deletes default value declarations such as int=0, boolean=false or empty strings
     */
    private static String toJsonExcludeDefaultValues(final Object obj)
    {
        return INSTANCE.toJson(obj)
                .replaceAll(" *\".*\": (0|false|\"\"),\n", "")
                .replaceAll(",\n *\".*\": (0|false|\"\")", "");
    }

    private final static Map<String /*path*/, Pair<Object, AtomicInteger>> REFERENCES =
            new HashMap<>();

    private final Class<?> type;

    private ObjectProperty<T> config =
            new SimpleObjectProperty<>(null);

    private final ReadOnlyStringProperty origin;

    private final String defaultConfig;

    private String path = null;

    public ConfigHolder(final String origin, final String defaultConfig, final Class<?> type)
    {
        this (new ReadOnlyStringWrapper(origin), defaultConfig, type);
    }

    public ConfigHolder(final ReadOnlyStringProperty origin, final String defaultConfig, final Class<?> type)
    {
        this.defaultConfig = defaultConfig;
        this.type = type;
        this.origin = origin;
    }

    /**
     * Gets the config<br>
     * If necessary load the config from file.
     */
    public ObjectProperty<T> get()
    {
        if (!origin.get().equals(path))
        {
            if (Objects.nonNull(config.get()))
                save();

            load();
        }

        return config;
    }

    /**
     * Loads the config from file, overwrites existing config
     */
    @SuppressWarnings("unchecked")
    public void load()
    {
        Pair<Object, AtomicInteger> ref = REFERENCES.get(path);
        if (Objects.isNull(ref))
        {
            Object object = null;

            System.out.println(String.format("DEBUG: Loading config file: %s", origin.get()));

            // If the config file could not be loaded use the default predefined file.
            try (final Reader reader = new InputStreamReader(
                   new FileInputStream(origin.get())))
            {
                object = INSTANCE.fromJson(reader, type);
            }
            catch (final Throwable t)
            {
                try (final Reader reader = new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(defaultConfig)))
                {
                    System.out.println(String.format("DEBUG: Error while loading provided config file %s, switched to default config %s!", origin.get(), defaultConfig));

                    object = INSTANCE.fromJson(reader, type);
                }
                catch (final Throwable tt)
                {
                    tt.printStackTrace();
                }
            }

            ref = new Pair<>(object, new AtomicInteger(0));
            REFERENCES.put(origin.get(), ref);

            System.out.println(String.format("DEBUG: Loaded config file: %s.", origin.get()));
        }
        else
            System.out.println(String.format("DEBUG: Reusing cached config file: %s.", origin.get()));

        path = origin.get();
        config.set((T) ref.first());
        ref.second().incrementAndGet();
    }

    /**
     * Saves the config to file.
     */
    @Override
    public void save()
    {
        final Pair<Object, AtomicInteger> ref = REFERENCES.get(path);
        if (ref.second().decrementAndGet() > 0)
            return;

        REFERENCES.remove(path);

        System.out.println(String.format("DEBUG: Saving config file %s.", path));

        // Notify inlined configs
        config.get().save();

        try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(path)))
        {
            writer.write(toString());
        }
        catch(final Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return toJsonExcludeDefaultValues(config.get());
    }
}
