
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import com.github.naios.wide.api.framework.storage.server.ServerStorageException;
import com.github.naios.wide.framework.internal.storage.name.NameStorageHolder;
import com.github.naios.wide.framework.internal.storage.name.NameStorageType;

@SuppressWarnings("serial")
class NoMetaEnumException extends ServerStorageException
{
    public NoMetaEnumException(final String name)
    {
        super(String.format("Field %s defines no valid metaenum!", name));
    }
}

public class AliasUtil
{
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Class<? extends Enum> getEnum(final String name)
    {
        Class<?> type = null;

        if (!name.isEmpty())
            try
            {
                type = Class.forName(/*TODO @FrameworkIntegration Classes.class.getPackage().getName() + "." + name*/null);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                throw new NoMetaEnumException(name);
            }

        if (type == null || !type.isEnum())
            throw new NoMetaEnumException(name);

        return (Class<? extends Enum>) type;
    }

    public static String getNamstorageEntry(final String name, final int entry)
    {
        final NameStorageType storage = NameStorageHolder.instance().get(name);
        if (storage == null)
            return null;

        final String value = storage.getStorage().request(entry);
        if (value == null)
            return null;
        else
            return storage.getPrefix() + value;
    }
}
