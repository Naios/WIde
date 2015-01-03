
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.EnviromentConfig;
import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.config.internal.config.main.ConfigImpl;
import com.github.naios.wide.config.internal.config.main.EnviromentConfigImpl;
import com.github.naios.wide.config.internal.util.ConfigHolder;

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

    private final static String PATH = System.getProperty(CONFIG_PATH_PROPERTY, CONFIG_PATH);

    /**
     * The config holder object that actually holds our data.
     */
    private final ConfigHolder<ConfigImpl> config =
            new ConfigHolder<>(CONFIG_PATH_DEFAULT, ConfigImpl.class);

    private final long DEFAULT_SAVE_INTERVAL_SECONDS = 300;

    private final String PROPERTY_SAVE_INTERVAL_SECONDS = "com.github.naios.config.saveinterval";

    private final long saveInterval = Long.valueOf(System.getProperty(PROPERTY_SAVE_INTERVAL_SECONDS, String.valueOf(DEFAULT_SAVE_INTERVAL_SECONDS)));

    private final String SAVER_THREAD_NAME = "WIde Config saver Thread";

    private final Thread saverThread = new Thread()
    {
        {
            setName(SAVER_THREAD_NAME);
        }

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(saveInterval));
                }
                catch (final Throwable e)
                {
                    return;
                }

                ConfigHolder.globalSave();
            }
        };
    };

    private static ConfigServiceImpl THIS_SERVICE;

    public static ConfigService getService()
    {
        return THIS_SERVICE;
    }

    /**
     * Command - Save
     */
    public void save()
    {
        ConfigHolder.globalSave();
    }

    /**
     * Command - Config (shows the config)
     */
    public void config()
    {
        System.out.println(config);
    }


	@Override
    public void reload()
	{
	    THIS_SERVICE = this;

	    config.load(PATH);
	    System.out.println(String.format("DEBUG: ConfigService::reload()"));

	    saverThread.start();
	    System.out.println(String.format("DEBUG: Config saver Thread started, interval %s seconds", saveInterval));
	}

	@Override
    public void close()
	{
	    saverThread.interrupt();
        System.out.println("DEBUG: Config saver Thread stopped!");

	    ConfigHolder.globalClose();
	    System.out.println(String.format("DEBUG: %s", "ConfigService::save()"));
    }

    @Override
    public ReadOnlyStringProperty title()
    {
        return config.get(PATH).get().title();
    }

    @Override
    public ReadOnlyStringProperty description()
    {
        return config.get(PATH).get().description();
    }

    @Override
    public StringProperty ui()
    {
        return config.get(PATH).get().ui();
    }

    @Override
    public StringProperty activeEnviroment()
    {
        return config.get(PATH).get().activeEnviroment();
    }

    @Override
    public List<EnviromentConfig> getEnviroments()
    {
        return config.get(PATH).get().getEnviroments();
    }

    @Override
    public QueryConfig getQueryConfig()
    {
        return config.get(PATH).get().getQueryConfig();
    }

    @Override
    public EnviromentConfigImpl getActiveEnviroment()
    {
        return config.get(PATH).get().getActiveEnviroment();
    }

    @Override
    public BooleanProperty compress()
    {
        return config.get(PATH).get().compress();
    }
}
