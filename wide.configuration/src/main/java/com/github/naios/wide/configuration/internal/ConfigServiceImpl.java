
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal;

import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.configuration.ConfigService;
import com.github.naios.wide.api.configuration.main.EnviromentConfig;
import com.github.naios.wide.api.configuration.main.QueryConfig;
import com.github.naios.wide.configuration.internal.config.ConfigImpl;
import com.github.naios.wide.configuration.internal.config.EnviromentConfigImpl;
import com.github.naios.wide.configuration.internal.util.ConfigHolder;

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
     * The config holder object that actually holds our data.
     */
    private final ConfigHolder<ConfigImpl> config =
            new ConfigHolder<>(System.getProperty(CONFIG_PATH_PROPERTY, CONFIG_PATH),
                    CONFIG_PATH_DEFAULT, ConfigImpl.class);

	@Override
    public void reload()
	{
	    config.load();
	    System.out.println(String.format("DEBUG: %s", "ConfigService::reload()"));
	}

	@Override
    public void save()
	{
	    config.save();
	    System.out.println(String.format("DEBUG: %s", "ConfigService::save()"));
    }

    @Override
    public ReadOnlyStringProperty title()
    {
        return config.get().get().title();
    }

    @Override
    public ReadOnlyStringProperty description()
    {
        return config.get().get().description();
    }

    @Override
    public StringProperty activeEnviroment()
    {
        return config.get().get().activeEnviroment();
    }

    @Override
    public List<EnviromentConfig> getEnviroments()
    {
        return config.get().get().getEnviroments();
    }

    @Override
    public QueryConfig getQueryConfig()
    {
        return config.get().get().getQueryConfig();
    }

    @Override
    public EnviromentConfigImpl getActiveEnviroment()
    {
        return config.get().get().getActiveEnviroment();
    }
}
