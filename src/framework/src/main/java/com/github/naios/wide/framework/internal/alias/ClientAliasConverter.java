
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
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageImpl;
import com.github.naios.wide.framework.internal.storage.client.ClientStorageSelector;
import com.github.naios.wide.framework.internal.storage.client.UnknownClientStorageStructure;

public class ClientAliasConverter implements AliasConverter
{
    @Override
    public Map<Integer, String> convertAlias(final Alias alias)
    {
        final Map<Integer, String> map = new HashMap<>();

        final ClientStorageImpl<UnknownClientStorageStructure> storage =
                new ClientStorageSelector<UnknownClientStorageStructure>
                        (name, ClientStoragePolicy.POLICY_ESTIMATE_ONLY).select();

        final Object[][] objects = storage.asObjectArray();
        for (int i = 0; i < storage.getRecordsCount(); ++i)
            map.put((int)objects[i][alias.entryColumnIndex().get()],
                    (String)objects[i][alias.nameColumnIndex().get()]);

        return map;
    }
}
