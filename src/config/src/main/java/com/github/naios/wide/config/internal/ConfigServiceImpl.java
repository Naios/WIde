
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.config.main.EnvironmentConfig;
import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.config.internal.config.main.ConfigImpl;

public final class ConfigServiceImpl implements ConfigService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private static EntityService entityService;

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

    // FIXME
    private final Thread saverThread = new Thread()
    {
        {
            setName(SAVER_THREAD_NAME);
        }

        @Override
        public void run()
        {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("WIde Config saver Thread started, interval {} seconds", saveInterval);

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

    public void setEntityService(final EntityService entityService)
    {
        ConfigServiceImpl.entityService = entityService;
    }

    public static EntityService getEntityService()
    {
        return entityService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExternalConfig(final String path, final Class<T> type, final Class<?>... interfaces)
    {
        return ((Optional<T>) ConfigHolder.getReference(path)).orElseGet(() ->
        {
            final T obj = new ConfigHolder<T>(path, type).get(path).get();
            return (T)Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler()
            {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
                {
                    if (method.getName().equals("toString"))
                        return ConfigHolder.toJsonExcludeDefaultValues(obj);

                    return method.invoke(obj, args);
                }
            });
        });
    }

    /**
     * OSGI Command
     */
    @Descriptor("Saves the WIde config to file.")
    public void save()
    {
        ConfigHolder.globalSave();
    }

    /**
     * OSGI Command
     */
    @Descriptor("Returns the WIde config and its subconfigs as Json.")
    public List<String> config()
    {
        return ConfigHolder.getConfigsAsList();
    }

	@Override
    public void reload()
	{
	    THIS_SERVICE = this;

	    config.load(PATH);

	    if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde Config Service stopped!");

	    saverThread.start();
	}

	@Override
    public void close()
	{
	    saverThread.interrupt();
	    if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde Config saver Thread stopped!");

	    ConfigHolder.globalClose();
	    if (LOGGER.isDebugEnabled())
            LOGGER.debug("WIde Config Service stopped!");
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
    public ReadOnlyStringProperty license()
    {
        return config.get(PATH).get().license();
    }

    @Override
    public StringProperty version()
    {
        return config.get(PATH).get().version();
    }

    @Override
    public StringProperty ui()
    {
        return config.get(PATH).get().ui();
    }

    @Override
    public StringProperty activeEnvironment()
    {
        return config.get(PATH).get().activeEnvironment();
    }

    @Override
    public Set<Entry<String, EnvironmentConfig>> getEnvironments()
    {
        return config.get(PATH).get().getEnvironments();
    }

    @Override
    public QueryConfig getQueryConfig()
    {
        return config.get(PATH).get().getQueryConfig();
    }

    @Override
    public EnvironmentConfig getActiveEnvironment()
    {
        return config.get(PATH).get().getActiveEnvironment();
    }

    @Override
    public BooleanProperty compress()
    {
        return config.get(PATH).get().compress();
    }
}
