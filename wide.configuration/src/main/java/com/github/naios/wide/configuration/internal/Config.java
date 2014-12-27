
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.util.GsonInstance;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class Config
{
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
            config = GsonInstance.INSTANCE.fromJson(reader, BaseConfig.class);
        }
        catch(final Throwable throwable)
        {
            try (final Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(
                            Constants.STRING_DEFAULT_CONFIG_NAME.toString())))
            {
                config = GsonInstance.INSTANCE.fromJson(reader, BaseConfig.class);
            }
            catch (final Throwable throwable2)
            {
                throwable2.printStackTrace();
            }
        }

	    loaded.set(true);

	    // Hooks.ON_CONFIG_LOADED
        WIde.getHooks().fire(Hook.ON_CONFIG_LOADED);
	}

	public void save()
	{
	    try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(Constants.STRING_DEFAULT_CONFIG_NAME.toString())))
        {
	        writer.write(GsonInstance.INSTANCE.toJson(config));
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
