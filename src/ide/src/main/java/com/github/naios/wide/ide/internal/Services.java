
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import com.github.naios.wide.api.config.ConfigService;
import com.github.naios.wide.api.database.DatabasePoolService;
import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.framework.FrameworkService;

/**
 * Helper class to access all required services
 */
public class Services
{
    private static ConfigService configService;

    private static DatabasePoolService databasePoolService;

    private static EntityService entityService;

    private static FrameworkService frameworkService;

    public static ConfigService getConfigService()
    {
        return configService;
    }

    public void setConfigService(final ConfigService configService)
    {
        Services.configService = configService;
    }

    public void setDatabasePoolService(final DatabasePoolService databasePoolService)
    {
        Services.databasePoolService = databasePoolService;
    }

    public static DatabasePoolService getDatabasePoolService()
    {
        return databasePoolService;
    }

    public void setEntityService(final EntityService entityService)
    {
        Services.entityService = entityService;
    }

    public static EntityService getEntityService()
    {
        return entityService;
    }

    public void setFrameworkService(final FrameworkService frameworkService)
    {
        Services.frameworkService = frameworkService;
    }

    public static FrameworkService getFrameworkService()
    {
        return frameworkService;
    }
}
