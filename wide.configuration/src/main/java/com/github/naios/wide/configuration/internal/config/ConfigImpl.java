
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.configuration.internal.config;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.configuration.main.Config;
import com.github.naios.wide.api.configuration.main.EnviromentConfig;
import com.github.naios.wide.api.configuration.main.QueryConfig;
import com.github.naios.wide.configuration.internal.util.Saveable;

@SuppressWarnings("serial")
class MissingActiveEnviroment extends RuntimeException
{
    public MissingActiveEnviroment()
    {
        super("Active enviroment config is missing!");
    }
}

public class ConfigImpl implements Config, Saveable
{
    private StringProperty title = new SimpleStringProperty(""),
                description = new SimpleStringProperty(""),
                    active_enviroment = new SimpleStringProperty("");

    private List<EnviromentConfigImpl> enviroments = new ArrayList<>();

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<EnviromentConfig> getEnviroments()
    {
        return (List)enviroments;
    }

    @Override
    public QueryConfig getQueryConfig()
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

    @Override
    public void save()
    {
        enviroments.forEach(e -> e.save());
        querys.save();
    }
}
