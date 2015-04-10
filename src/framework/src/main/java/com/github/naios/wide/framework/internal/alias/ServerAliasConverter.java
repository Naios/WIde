
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.alias;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.api.config.alias.Alias;
import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.framework.FrameworkWorkspace;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;

public class ServerAliasConverter implements AliasConverter
{
    @Override
    public Map<Integer, String> convertAlias(final Alias alias, final FrameworkWorkspace workspace)
    {
        final Map<Integer, String> map = new HashMap<>();

        final Database database = FrameworkServiceImpl.getDatabasePoolService().requestConnection(alias.database().get()).get();
        try (final ResultSet result = database.execute("SELECT %s, %s FROM %s",
                alias.entryColumn().get(), alias.nameColumn().get(), alias.target().get()))
        {
            while (result.next())
                map.put(result.getInt(1), result.getString(2));

            result.close();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return map;
    }
}
