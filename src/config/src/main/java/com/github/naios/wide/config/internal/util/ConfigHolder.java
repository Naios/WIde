
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.framework.storage.client.ClientStorageFormatImpl;
import com.github.naios.wide.api.util.IdentitySet;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.config.internal.ConfigServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.MalformedJsonException;

class Reference
{
    private final Object object;

    @SuppressWarnings("rawtypes")
    private final Set<ConfigHolder> references =
            new IdentitySet<>();

    private int lastHashCode = 0;

    public Reference(final Object object)
    {
        this.object = object;
    }

    @SuppressWarnings({ "rawtypes" })
    public void addReference(final ConfigHolder reference)
    {
        references.add(reference);
    }

    @SuppressWarnings({ "rawtypes" })
    public boolean removeReference(final ConfigHolder reference)
    {
        references.remove(reference);
        return !references.isEmpty();
    }

    public Object getObject()
    {
        return object;
    }

    public int getLastHashCode()
    {
        return lastHashCode;
    }

    public void setLastHashCode(final int lastHashCode)
    {
        this.lastHashCode = lastHashCode;
    }

    public void close()
    {
        references.forEach(holder -> holder.close());
    }
}

public class ConfigHolder<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHolder.class);

    /**
     * The {@link Gson} instance used in this bundle<br>
     * Including registered type adapters for javafx propertys and pretty print set
     */
    private final static Gson INSTANCE = new GsonBuilder()
        // Pretty print
        .setPrettyPrinting()
        // Enable Map Key Serialization
        .enableComplexMapKeySerialization()
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

    public static String toJson(final Object obj)
    {
        return INSTANCE.toJson(obj);
    }

    /**
     * Converts an object to json<br>
     * Deletes default value declarations such as int=0, boolean=false or empty strings
     */
    public static String toJsonExcludeDefaultValues(final Object obj)
    {
        return toJson(obj)
                .replaceAll(" *\".*\": (0|false|\"\"),\n", "")
                .replaceAll(",\n *\".*\": (0|false|\"\")", "");
    }

    // TODO convert this into a concurrent hash map
    private final static Map<String /*path*/, Reference> REFERENCES =
            new ConcurrentHashMap<>();

    private final Class<?> type;

    private ObjectProperty<T> config =
            new SimpleObjectProperty<>(null);

    private final String defaultConfig;

    private String path = null;

    public ConfigHolder(final String defaultConfig, final Class<?> type)
    {
        this.defaultConfig = defaultConfig;
        this.type = type;
    }

    /**
     * Gets the config<br>
     * If necessary load the config from file.
     */
    public ObjectProperty<T> get(final String origin)
    {
        if (!origin.equals(path))
        {
            if (Objects.nonNull(config.get()))
            {
                save(path, REFERENCES.get(path));
            }

            load(origin);
        }

        return config;
    }

    /**
     * Loads the config from file, overwrites existing config
     */
    @SuppressWarnings("unchecked")
    public void load(final String origin)
    {
        Reference ref = REFERENCES.get(origin);
        if (Objects.isNull(ref))
        {
            Object object = null;

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Loading config file: {}", origin);

            // If the config file could not be loaded use the default predefined file.
            try (final Reader reader = new InputStreamReader(
                   new FileInputStream(origin)))
            {
                object = INSTANCE.fromJson(reader, type);
            }
            catch (final MalformedJsonException e)
            {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(e.getMessage());
                throw new Error(e);
            }
            catch (final Exception e)
            {
                try (final Reader reader = new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(defaultConfig)))
                {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Error while loading provided config file {}, switched to default config {} ({})!", origin, defaultConfig, e.getMessage());

                    object = INSTANCE.fromJson(reader, type);
                }
                catch (final Throwable tt)
                {
                    tt.printStackTrace();
                }
            }

            ref = new Reference(object);
            REFERENCES.put(origin, ref);
        }
        else if (LOGGER.isDebugEnabled())
                LOGGER.debug("Reusing cached config file: {}.", origin);

        synchronized (ref.getObject())
        {
            path = origin;
            config.set((T) ref.getObject());
            ref.addReference(this);
        }
    }

    /**
     * Saves the config to file.
     */
    public static void save(final String path, final Reference ref)
    {
        Objects.requireNonNull(path);
        Objects.requireNonNull(ref);

        synchronized (ref.getObject())
        {
            final String json = getJsonOfObject(ref.getObject(), ConfigServiceImpl.getService().compress().get());

            if (json.hashCode() == ref.getLastHashCode())
            {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Skipped saving of config file {}, nothing to save!", path);

                return;
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Saving config file {}.", path);

            // Create directories
            final File file = new File(path);
            if (Objects.nonNull(file.getParent()))
                new File(file.getParent()).mkdirs();

            try (final Writer writer = new OutputStreamWriter(
                    new FileOutputStream(file)))
            {
                ref.setLastHashCode(json.hashCode());
                writer.write(json);
            }
            catch(final Throwable throwable)
            {
                throwable.printStackTrace();
            }
        }
    }

    public void close()
    {
        if (Objects.isNull(config.get()))
            return;

        synchronized (REFERENCES)
        {
            final Reference ref = REFERENCES.get(path);

            // If there are references alive, release this reference and continue
            if (ref.removeReference(this))
            {
                config.set(null);
                return;
            }

            REFERENCES.remove(path);

            save(path, ref);
        }

        config.set(null);
        path = null;
    }

    public static String getJsonOfObject(final Object object, final boolean compress)
    {
        if (compress)
            return toJsonExcludeDefaultValues(object);
        else
            return toJson(object);
    }

    @Override
    public String toString()
    {
        return toJsonExcludeDefaultValues(config.get());
    }

    public static void globalSave()
    {
        synchronized (REFERENCES)
        {
            REFERENCES.entrySet().forEach(entry -> save(entry.getKey(), entry.getValue()));
        }
    }

    public static void globalClose()
    {
        synchronized (REFERENCES)
        {
            REFERENCES.values().forEach(entry -> entry.close());
        }
    }

    public static List<String> getConfigsAsList()
    {
        final List<String> result = new ArrayList<>();
        synchronized (REFERENCES)
        {
            REFERENCES.forEach((path, reference) ->
            {
                result.add(String.format("Config File: %s (Hash: %s)\n%s", path,
                        StringUtil.asHex(reference.getLastHashCode()), reference.getObject()));
            });
        }
        return result;
    }
}
