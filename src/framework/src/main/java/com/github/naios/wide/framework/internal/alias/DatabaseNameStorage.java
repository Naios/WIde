
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.alias;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.database.DatabaseType;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public class DatabaseNameStorage extends NameStorage
{
    private final String table, entry, name;

    public DatabaseNameStorage(final String table, final String entry, final String name)
    {
        this.table = table;
        this.entry = entry;
        this.name = name;

        load();
    }

    @Override
    public void load()
    {
        /*TODO @FrameworkIntegration:Trace
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Loading Database Namstorage: %s", table));*/

        final Database database = FrameworkServiceImpl.getDatabasePoolService().requestConnection(DatabaseType.WORLD.getId()).get();
        try (final ResultSet result = database.execute("SELECT %s, %s FROM %s", entry, name, table))
        {
            while (result.next())
                add(result.getInt(1), result.getString(2));

            result.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }
}
