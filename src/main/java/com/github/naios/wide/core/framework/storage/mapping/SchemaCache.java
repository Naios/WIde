
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.util.GsonInstance;

public class SchemaCache
{
    public final static SchemaCache INSTANCE =
            new SchemaCache();

    private final Map<String, Schema> cache =
            new HashMap<>();

    /**
     * Returns the schema of the active environment
     */
    public Schema getSchemaOfActiveEnviroment(final String databaseID)
    {
        final String path = WIde.getConfig().get().getActiveEnviroment()
                .getDatabaseConfig(databaseID).schema().get();

        return get(path);
    }

    /**
     * Returns the schema at the given path.
     */
    public Schema get(final String path)
    {
        Schema schema = cache.get(path);

        if (schema == null)
            try (final Reader reader = new FileReader(path))
            {
                schema = GsonInstance.INSTANCE.fromJson(reader, Schema.class);
            }
            catch (final Throwable throwable)
            {
                throw new Error(throwable);
            }

        return schema;
    }
}
