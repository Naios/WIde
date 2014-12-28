
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;

public class ConfigHolder<T extends Saveable> implements Saveable
{
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
        System.out.println(String.format("DEBUG: Loading config file: %s", origin.get()));

        // If the config file could not be loaded use the default predefined file.
        try (final Reader reader = new InputStreamReader(
               new FileInputStream(origin.get())))
        {
            config.set((T) GsonHelper.INSTANCE.fromJson(reader, type));
        }
        catch (final Throwable t)
        {
            try (final Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(defaultConfig)))
            {
                System.out.println(String.format("DEBUG: Error while loading provided config file %s, switched to default config %s!", origin.get(), defaultConfig));

                config.set((T) GsonHelper.INSTANCE.fromJson(reader, type));
            }
            catch (final Throwable tt)
            {
                tt.printStackTrace();
            }
        }

        path = origin.get();
    }

    /**
     * Saves the config to file.
     */
    @Override
    public void save()
    {
        System.out.println(String.format("DEBUG: Saving config file %s.", path));

        // Notify inlined configs
        config.get().save();

        try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(path)))
        {
            writer.write(GsonHelper.toJsonExcludeDefaultValues(config));
        }
        catch(final Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
}
