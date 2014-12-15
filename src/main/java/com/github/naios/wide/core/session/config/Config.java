
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.util.PropertyJSONAdapter;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

public class Config
{
    private final static Gson GSON = new GsonBuilder()
        // Pretty print
        .setPrettyPrinting()
        // Exclude static fields
        .excludeFieldsWithModifiers(Modifier.STATIC)
        // StringProperty Adapter
        .registerTypeAdapter(StringProperty.class,
                new PropertyJSONAdapter<>(
                        (observable, json) -> observable.set(json.getAsJsonPrimitive().getAsString()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleStringProperty()))
        // IntegerProperty Adapter
        .registerTypeAdapter(IntegerProperty.class,
                new PropertyJSONAdapter<>(
                        (observable, json) -> observable.set(json.getAsJsonPrimitive().getAsInt()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleIntegerProperty()))
        // FloatProperty Adapter
        .registerTypeAdapter(FloatProperty.class,
                new PropertyJSONAdapter<>(
                        (observable, json) -> observable.set(json.getAsJsonPrimitive().getAsFloat()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleFloatProperty()))
        // BooleanProperty Adapter
        .registerTypeAdapter(BooleanProperty.class,
                new PropertyJSONAdapter<>(
                        (observable, json) -> observable.set(json.getAsJsonPrimitive().getAsBoolean()),
                            (observable) -> new JsonPrimitive(observable.get()),
                                () -> new SimpleBooleanProperty()))
        .create();

    private BaseConfig config;

    private final BooleanProperty loaded =
            new SimpleBooleanProperty();

	public Config()
	{
	    // After Arg parser has finished, load config with args
        WIde.getHooks().addListener(new HookListener(Hook.ON_ENVIROMENT_LOADED, this)
        {
            @Override
            public void informed()
            {
                load();
            }
        });

	    // Saves Config on Close
	    WIde.getHooks().addListener(new HookListener(Hook.ON_APPLICATION_STOP, this)
        {
            @Override
            public void informed()
            {
                save();
            }
        });
	}

	private void load()
	{
	    // If the config file could not be loaded use the default predefined file.
	    try (final Reader reader = new InputStreamReader(
	            new FileInputStream(Constants.STRING_DEFAULT_CONFIG_NAME.toString())))
        {
            config = GSON.fromJson(reader, BaseConfig.class);
        }
        catch(final Throwable throwable)
        {
            try (final Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(
                            Constants.STRING_DEFAULT_CONFIG_NAME.toString())))
            {
                config = GSON.fromJson(reader, BaseConfig.class);
            }
            catch (final Throwable throwable2)
            {
                throwable2.printStackTrace();
            }
        }

	    // Hooks.ON_CONFIG_LOADED
        WIde.getHooks().fire(Hook.ON_CONFIG_LOADED);
	}

	public void save()
	{
	    try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(Constants.STRING_DEFAULT_CONFIG_NAME.toString())))
        {
	        writer.write(GSON.toJson(config));
        }
        catch(final Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public boolean isLoaded()
    {
        return loaded.get();
    }

    public BaseConfig get()
    {
        return config;
    }
}
