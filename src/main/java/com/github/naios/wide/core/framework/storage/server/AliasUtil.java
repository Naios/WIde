
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

public class AliasUtil
{
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Class<? extends Enum> getEnum(final Field field)
    {
        final EnumAlias annotation = field.getAnnotation(EnumAlias.class);
        if (annotation == null)
            throw new NoMetaEnumException(field);

        Class<?> type = null;

        if (!annotation.value().isEmpty())
            try
            {
                type = Class.forName(Classes.class.getPackage().getName() + "." + annotation.value());
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }

        if (type == null || !type.isEnum())
            throw new NoMetaEnumException(field);

        return (Class<? extends Enum>) type;
    }

    public static String getNamstorageEntry(final Field field, final int entry)
    {
        final NamestorageAlias annotation = field.getAnnotation(NamestorageAlias.class);
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
