
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.alias;

import com.github.naios.wide.api.framework.storage.client.ClientStorageException;

public class ClientNameStorage extends NameStorage
{
    private final int entryColumn, nameColumn;

    private final String name;

    public ClientNameStorage(final String name, final int entryColumn, final int nameColumn)
    {
        this.name = name;
        this.entryColumn = entryColumn;
        this.nameColumn = nameColumn;

        load();
    }

    @Override
    public void load()
    {
        /*TODO @FrameworkIntegration:Trace
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Loading Client Namstorage: %s", name));*/

        try
        {

        }
        catch (final ClientStorageException e)
        {
            e.printStackTrace();
        }
    }
}
