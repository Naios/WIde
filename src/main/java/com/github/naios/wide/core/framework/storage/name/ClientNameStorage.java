
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
import com.github.naios.wide.core.framework.storage.client.ClientStorageSelector;
import com.github.naios.wide.core.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.core.framework.storage.client.UnknownClientStorageStructure;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class ClientNameStorage extends NameStorage
{
    private final int entryColumn, nameColumn;

    private final String name;

    public ClientNameStorage(final String name, final int entryColumn, final int nameColumn)
    {
        this.name = ClientStorageStructure.getPathOfFile(name);
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
            final ClientStorage<UnknownClientStorageStructure> dbc =
                    new ClientStorageSelector<UnknownClientStorageStructure>(UnknownClientStorageStructure.class, name).select();

            dbc.fillNameStorage(storage, entryColumn, nameColumn);
        }
        catch (final ClientStorageException e)
        {
            e.printStackTrace();
        }
    }
}
