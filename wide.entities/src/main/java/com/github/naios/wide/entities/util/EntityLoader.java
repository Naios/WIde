
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.util;

import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public class EntityLoader
{
    private final static Object DUMMY = new Object();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class<? extends Enum> requestEnum(final String name) throws NoSucheEntityException
    {
        try
        {
            final Class<?> type = Class.forName(name, true, DUMMY.getClass().getClassLoader());
            if (!type.isEnum())
                throw new NoSucheEntityException(name, Enum.class);

            return (Class)type;
        }
        catch (final ClassNotFoundException e)
        {
            throw new NoSucheEntityException(name, e);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }
    }

    // TODO Fix double code
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class<? extends ClientStorageStructure> requestClientStorage(final String name) throws NoSucheEntityException
    {
        try
        {
            final Class<?> type = Class.forName(name, true, DUMMY.getClass().getClassLoader());
            if (!ClientStorageStructure.class.isAssignableFrom(type))
                throw new NoSucheEntityException(name, ClientStorageStructure.class);

            return (Class)type;
        }
        catch (final ClassNotFoundException e)
        {
            throw new NoSucheEntityException(name, e);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class<? extends ServerStorageStructure> requestServerStorage(final String name) throws NoSucheEntityException
    {
        try
        {
            final Class<?> type = Class.forName(name, true, DUMMY.getClass().getClassLoader());
            if (!ServerStorageStructure.class.isAssignableFrom(type))
                throw new NoSucheEntityException(name, ServerStorageStructure.class);

            return (Class)type;
        }
        catch (final ClassNotFoundException e)
        {
            throw new NoSucheEntityException(name, e);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }
    }
}
