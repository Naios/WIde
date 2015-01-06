
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.config.internal.config.alias;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.WIdeConstants;
import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.config.alias.AliasConfig;

@SuppressWarnings("serial")
class MissingAliasException extends RuntimeException
{
    public MissingAliasException(final String name)
    {
        super(String.format("Alias %s is missing!!", name));
    }
}

public class AliasConfigImpl implements AliasConfig
{
    private StringProperty name = new SimpleStringProperty(""),
            desciption = new SimpleStringProperty(""),
                    version = new SimpleStringProperty(WIdeConstants.VERSION_WIDE_ALIAS_CONFIG.toString());

    private Map<String, AliasImpl> aliases = new HashMap<>();

    @Override
    public StringProperty name()
    {
        return name;
    }

    @Override
    public StringProperty description()
    {
        return desciption;
    }

    @Override
    public StringProperty version()
    {
        return version;
    }

    @Override
    public Alias getAliasForName(final String name)
    {
        final Alias alias = aliases.get(name);
        if (Objects.nonNull(alias))
            return alias;
        else
            throw new MissingAliasException(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Entry<String, Alias>> getAliases()
    {
        return (Set)aliases.entrySet();
    }
}
