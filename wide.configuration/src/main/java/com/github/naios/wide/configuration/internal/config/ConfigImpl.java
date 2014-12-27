
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.configuration.Config;

@SuppressWarnings("serial")
class MissingActiveEnviroment extends RuntimeException
{
    public MissingActiveEnviroment()
    {
        super("Active enviroment config is missing!");
    }
}

public class ConfigImpl implements Config
{
    private StringProperty title, description, active_enviroment;

    private List<EnviromentConfigImpl> enviroments;

    private QueryConfigImpl querys;

    @Override
    public ReadOnlyStringProperty title()
    {
        return title;
    }

    @Override
    public ReadOnlyStringProperty description()
    {
        return description;
    }

    @Override
    public StringProperty activeEnviroment()
    {
        return active_enviroment;
    }

    @Override
    public List<EnviromentConfigImpl> getEnviroments()
    {
        return enviroments;
    }

    @Override
    public QueryConfigImpl getQueryConfig()
    {
        return querys;
    }

    @Override
    public EnviromentConfigImpl getActiveEnviroment()
    {
        for (final EnviromentConfigImpl env : enviroments)
            if (env.name().get().equals(active_enviroment.get()))
                return env;

        throw new MissingActiveEnviroment();
    }
}
