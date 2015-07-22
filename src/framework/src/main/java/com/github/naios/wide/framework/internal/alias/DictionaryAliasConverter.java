
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.alias;

import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.framework.FrameworkWorkspace;

public class DictionaryAliasConverter implements AliasConverter
{
    @Override
    public Map<Integer, String> convertAlias(final Alias alias, final FrameworkWorkspace workspace)
    {
        return new HashMap<>(alias.customEntries());
    }
}
