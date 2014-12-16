
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;

import com.github.naios.wide.core.framework.game.Classes;
import com.github.naios.wide.core.framework.storage.name.NameStorageHolder;
import com.github.naios.wide.core.framework.storage.name.NameStorageType;

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
    @SuppressWarnings({ "rawtypes" })
    public static Class<? extends Enum> getEnum(final Field field)
    {
        final EnumAlias annotation = field.getAnnotation(EnumAlias.class);
        if (annotation == null)
            throw new NoMetaEnumException(field.getName());

        return getEnum(annotation.value());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Class<? extends Enum> getEnum(final String name)
    {
        Class<?> type = null;

        if (!name.isEmpty())
            try
            {
                type = Class.forName(Classes.class.getPackage().getName() + "." + name);
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

    public static String getNamstorageEntry(final Field field, final int entry)
    {
        final NameAlias annotation = field.getAnnotation(NameAlias.class);
        if (annotation == null)
            return null;

        final NameStorageType storage = NameStorageHolder.instance().get(annotation.value());
        if (storage == null)
            return null;

        final String name = storage.getStorage().request(entry);
        if (name == null)
            return null;
        else
            return storage.getPrefix() + name;
    }
}
