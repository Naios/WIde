
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.configuration.ConfigService;
import com.github.naios.wide.configuration.EnviromentConfig;
import com.github.naios.wide.configuration.QueryConfig;
import com.github.naios.wide.configuration.internal.config.ConfigImpl;
import com.github.naios.wide.configuration.internal.config.EnviromentConfigImpl;
import com.github.naios.wide.configuration.internal.util.GsonInstance;

public class ConfigServiceImpl implements ConfigService
{
    /**
     * Declares the name of our config file to load
     */
    private final static String CONFIG_NAME = "WIde.json";

    /**
     * Declares the path of our default config file
     */
    private final static String DEFAULT_CONFIG_PATH = "default/WIde.json";

    /**
     * The config object that actually holds our data.
     */
    private ConfigImpl config;

	@Override
    public void reload()
	{
	    // If the config file could not be loaded use the default predefined file.
	    try (final Reader reader = new InputStreamReader(
               new FileInputStream(CONFIG_NAME)))
        {
            config = GsonInstance.INSTANCE.fromJson(reader, ConfigImpl.class);
        }
        catch(final Throwable t)
        {
            try (final Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH)))
            {
                config = GsonInstance.INSTANCE.fromJson(reader, ConfigImpl.class);
            }
            catch (final Throwable tt)
            {
                tt.printStackTrace();
            }
        }
	}

	@Override
    public void save()
	{
	    try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(CONFIG_NAME)))
        {
	        writer.write(GsonInstance.INSTANCE.toJson(config));
        }
        catch(final Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    @Override
    public ReadOnlyStringProperty title()
    {
        return config.title();
    }

    @Override
    public ReadOnlyStringProperty description()
    {
        return config.description();
    }

    @Override
    public StringProperty activeEnviroment()
    {
        return config.activeEnviroment();
    }

    @Override
    public List<EnviromentConfig> getEnviroments()
    {
        return config.getEnviroments();
    }

    @Override
    public QueryConfig getQueryConfig()
    {
        return config.getQueryConfig();
    }

    @Override
    public EnviromentConfigImpl getActiveEnviroment()
    {
        return config.getActiveEnviroment();
    }
}
