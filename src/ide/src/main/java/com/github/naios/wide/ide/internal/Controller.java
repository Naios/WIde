
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /**
     * The system property which prevents the gui from starting.
     */
    private static final String WIDE_NO_GUI_PROPERTY = "wide.nogui";

    private static BundleContext bundleContext;

    private static Controller instance;

    public void setBundleContext(final BundleContext bundleContext)
    {
        Controller.bundleContext = bundleContext;
    }

    public static Controller getInstance()
    {
        return instance;
    }

    public static void shutdownFramework()
    {
        try
        {
            bundleContext.getBundle(0).stop();
        }
        catch (final BundleException e)
        {
            e.printStackTrace();
        }
    }

    public void start()
    {
        // Don't access the services here!

        instance = this;

        System.out.println(String.format("The logging property: %s", bundleContext.getProperty("org.ops4j.pax.logging.DefaultServiceLog.level")));

        if (!Boolean.getBoolean(WIDE_NO_GUI_PROPERTY))
            new Thread()
            {
                {
                    setDaemon(true);
                }

                @Override
                public void run()
                {
                    try
                    {
                        // Start Application
                        Application.launch(Application.class);
                    }
                    catch (final Throwable e)
                    {
                        e.printStackTrace();
                    }
                };

            }.start();

        LOGGER.info("Started WIde IDE service.");
    }

    public void stop()
    {
        LOGGER.info("Stopped WIde IDE service.");
    }
}
