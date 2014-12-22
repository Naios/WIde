
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.name;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.client.ClientStorage;
import com.github.naios.wide.core.framework.storage.client.ClientStorageException;
import com.github.naios.wide.core.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.core.framework.storage.client.UnknownClientStorageStructure;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class ClientNameStorage extends NameStorage
{
    private final int entryColumn, nameColumn;

    private final String name;

    public ClientNameStorage(final String name, final int entryColumn, final int nameColumn)
    {
        this.name = name;
        this.entryColumn = entryColumn;
        this.nameColumn = nameColumn;

        setup();
    }

    @Override
    public void setup()
    {
        WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_LOADED, this)
        {
            @Override
            public void informed()
            {
                load();
            }
        });

        if (WIde.getConfig().isLoaded())
            load();
    }

    @Override
    public void load()
    {
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Loading Client Namstorage: %s", name));

        try
        {
            final ClientStorage<UnknownClientStorageStructure> storage =
                    new ClientStorageSelector<UnknownClientStorageStructure>
                            (ClientStorage.getPathForStorage(name),
                                ClientStoragePolicy.POLICY_ESTIMATE_ONLY).select();

            final Object[][] objects = storage.asObjectArray();
            for (int i = 0; i < storage.getRecordsCount(); ++i)
                add((int)objects[i][entryColumn], (String)objects[i][nameColumn]);
        }
        catch (final ClientStorageException e)
        {
            e.printStackTrace();
        }
    }
}
