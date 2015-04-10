
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework;

import com.github.naios.wide.api.config.main.EnviromentConfig;

/**
 * The Framework Service is exported as OSGI service.
 * It allows you to create new {@link FrameworkWorkspace}s.
 */
public interface FrameworkService
{
    /**
     * Creates a new fresh {@link FrameworkWorkspace}.
     * @param config The {@link EnviromentConfig} you want to use.
     * @return Returns a new {@link FrameworkWorkspace} which allows you to work with.
     */
    public FrameworkWorkspace createWorkspaceFromEnviroment(EnviromentConfig config);
}
