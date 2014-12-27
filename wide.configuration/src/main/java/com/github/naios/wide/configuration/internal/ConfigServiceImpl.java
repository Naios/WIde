
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

public final class ConfigServiceImpl implements ConfigService
{
    /**
     * Declares the name of our config file to load
     */
    private final static String CONFIG_PATH = "WIde.json";

    /**
     * Property to allow to overwrite the config path
     */
    private final static String CONFIG_PATH_PROPERTY = "com.github.naios.wide.config";

    /**
     * Declares the path of our default config file
     */
    private final static String CONFIG_PATH_DEFAULT = "default/WIde.json";

    /**
     * The config object that actually holds our data.
     */
    private ConfigImpl config;

	@Override
    public void reload()
	{
	    // Get the config path through a system property or the default relative filename
	    final String path = System.getProperty(CONFIG_PATH_PROPERTY, CONFIG_PATH);

	    // If the config file could not be loaded use the default predefined file.
	    try (final Reader reader = new InputStreamReader(
               new FileInputStream(path)))
        {
            config = GsonInstance.INSTANCE.fromJson(reader, ConfigImpl.class);
        }
        catch (final Throwable t)
        {
            try (final Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(CONFIG_PATH_DEFAULT)))
            {
                config = GsonInstance.INSTANCE.fromJson(reader, ConfigImpl.class);
            }
            catch (final Throwable tt)
            {
                tt.printStackTrace();
            }
        }

	    System.out.println(String.format("DEBUG: %s", "ConfigService::reload()"));
	}

	@Override
    public void save()
	{
	    try (final Writer writer = new OutputStreamWriter(
                new FileOutputStream(CONFIG_PATH)))
        {
	        writer.write(GsonInstance.INSTANCE.toJson(config));
        }
        catch(final Throwable throwable)
        {
            throwable.printStackTrace();
        }

	    System.out.println(String.format("DEBUG: %s", "ConfigService::save()"));
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
