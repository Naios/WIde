
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import javafx.application.Application;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class WIdeGUIController
{
    private static final String WIDE_NO_GUI_PROPERTY = "wide.nogui";

    private static BundleContext bundleContext;

    private static WIdeGUIController instance;

    public void setBundleContext(final BundleContext bundleContext)
    {
        WIdeGUIController.bundleContext = bundleContext;
    }

    public static WIdeGUIController getInstance()
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
        instance = this;

        if (!Boolean.getBoolean(WIDE_NO_GUI_PROPERTY))
            new Thread()
            {
                @Override
                public void run()
                {
                    // Start Application
                    Application.launch(WIdeGUIApplication.class);
                };

            }.start();
    }

    public void stop()
    {

    }
}
