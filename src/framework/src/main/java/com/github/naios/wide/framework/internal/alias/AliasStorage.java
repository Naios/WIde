
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.alias;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.config.alias.AliasType;
import com.github.naios.wide.api.framework.AliasFactory;
import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.api.util.Pair;

public class AliasStorage implements AliasFactory
{
    private final FrameworkWorkspace workspace;

    public AliasStorage(final FrameworkWorkspace workspace)
    {
        this.workspace = workspace;
    }

    @SuppressWarnings("serial")
    private static final Map<AliasType, AliasConverter> ALIAS_CONVERTER =
            new HashMap<AliasType, AliasConverter>()
    {
        {
            put(AliasType.CLIENT, new ClientAliasConverter());
            put(AliasType.SERVER, new ServerAliasConverter());
            put(AliasType.ENUM, new EnumAliasConverter());
            put(AliasType.DICTIONARY, new DictionaryAliasConverter());
            put(AliasType.EMPTY, new EmptyAliasConverter());

        }
    };

    private final Map<String, Pair<Alias, Map<Integer, String>>> aliases = new HashMap<>();

    private Pair<Alias, Map<Integer, String>> createStorage(final String name)
    {
        final Alias alias = workspace.getEnvironmentConfig()
                .getAliasDefinitionConfig().get().getAliasForName(name);

        if (Objects.isNull(alias))
            throw new IllegalArgumentException(String.format("Alias %s isn't defined!", alias));

        final AliasConverter converter = ALIAS_CONVERTER.get(alias.getAliasType());
        final Map<Integer, String> entries = converter.convertAlias(alias, workspace);
        entries.putAll(alias.customEntries());

        return new Pair<>(alias, Collections.unmodifiableMap(entries));
    }

    private Pair<Alias, Map<Integer, String>> createAliasMapOrGet(final String name)
    {
        Pair<Alias, Map<Integer, String>> alias = aliases.get(name);
        if (Objects.isNull(alias))
        {
            alias = createStorage(name);
            aliases.put(name, alias);
        }

        return alias;
    }

    private static String createPrefix(final String prefix)
    {
        if (Objects.isNull(prefix) || prefix.isEmpty())
            return "";
        else
            return prefix + " ";
    }

    private String createAliasFromMap(final Pair<Alias, Map<Integer, String>> map, final int value)
    {
        final String alias = map.second().get(value);

        // Fail prefix
        if (Objects.isNull(alias))
            return createPrefix(map.first().prefix().get()) +
                    String.format(map.first().failPrefix().get(), value);

        return createPrefix(map.first().prefix().get()) + alias;
    }

    @Override
    public String requestAlias(final String name, final int value)
    {
        return createAliasFromMap(createAliasMapOrGet(name), value);
    }

    @Override
    public Map<Integer, String> requestAllAliases(final String name)
    {
        return createAliasMapOrGet(name).second();
    }

    @Override
    public void reloadAliases()
    {
        aliases.replaceAll((name, pair) ->
        {
            switch (pair.first().getAliasType())
            {
                case CLIENT:
                case SERVER:
                    return createStorage(name);
                default:
                    return pair;
            }
        });
    }
}
