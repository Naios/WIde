
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.main.Config;
import com.github.naios.wide.api.config.main.EnviromentConfig;
import com.github.naios.wide.api.config.main.QueryConfig;
import com.github.naios.wide.config.internal.util.ConfigHolder;

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
    private StringProperty title = new SimpleStringProperty(""),
                description = new SimpleStringProperty(""),
                    ui = new SimpleStringProperty(Config.DEFAULT_UI_AUTO),
                        active_enviroment = new SimpleStringProperty("");

    private Map<String, EnviromentConfigImpl> enviroments =
            new HashMap<String, EnviromentConfigImpl>();

    private QueryConfigImpl querys;

    private BooleanProperty compress = new SimpleBooleanProperty(true);

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
    public StringProperty ui()
    {
        return ui;
    }

    @Override
    public StringProperty activeEnviroment()
    {
        return active_enviroment;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Entry<String, EnviromentConfig>> getEnviroments()
    {
        return (Set)enviroments.entrySet();
    }

    @Override
    public QueryConfig getQueryConfig()
    {
        return querys;
    }

    @Override
    public BooleanProperty compress()
    {
        return compress;
    }

    @Override
    public EnviromentConfig getActiveEnviroment()
    {
        final EnviromentConfig enviroment = enviroments.get(active_enviroment.get());
        if (Objects.nonNull(enviroment))
            return enviroment;
        else
            throw new MissingActiveEnviroment();
    }

    @Override
    public String toString()
    {
        return ConfigHolder.toJsonExcludeDefaultValues(this);
    }
}
