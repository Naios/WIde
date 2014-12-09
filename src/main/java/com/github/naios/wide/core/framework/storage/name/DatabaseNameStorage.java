
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.name;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.session.database.DatabaseType;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class DatabaseNameStorage extends NameStorage
{
    private final String table, entry, name;

    public DatabaseNameStorage(final String table, final String entry, final String name)
    {
        this.table = table;
        this.entry = entry;
        this.name = name;

        setup();
    }

    @Override
    public void setup()
    {
        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_ESTABLISHED, this)
        {
            @Override
            public void informed()
            {
                load();
            }
        });

        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_CLOSE, this)
        {
            @Override
            public void informed()
            {
                unload();
            }
        });

        if (WIde.getDatabase().isConnected())
            load();
    }

    @Override
    public void load()
    {
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Loading Database Namstorage: %s", table));

        try (final Statement stmt = WIde.getDatabase()
                .connection(DatabaseType.WORLD).get().createStatement())
        {
            final ResultSet result = stmt.executeQuery(String.format(
                    "SELECT %s, %s FROM %s", entry, name, table));

            while (result.next())
                storage.put(result.getInt(1), result.getString(2));

            result.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }
}
