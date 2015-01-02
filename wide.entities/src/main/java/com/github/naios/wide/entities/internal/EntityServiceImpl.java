
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.internal;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.entities.NoSucheEntityException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.entities.enums.Classes;

public class EntityServiceImpl implements EntityService
{
    private Bundle bundle;

    private final Map<String, Class<?>> classes =
            new HashMap<>();

    public void setBundle(final Bundle bundle)
    {
        this.bundle = bundle;
    }

    @Override
    public Class<?> requestClass(final String fullName) throws NoSucheEntityException
    {
        Class<?> type = classes.get(fullName);
        if (type != null)
            return type;

        try
        {
            type = bundle.loadClass(fullName);
            classes.put(fullName, type);
            return type;
        }
        catch (final ClassNotFoundException e)
        {
            throw new NoSucheEntityException(fullName, e);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<? extends Enum> requestEnum(final String shortName) throws NoSucheEntityException
    {
        final Class<?> type;
        try
        {
            type = requestClass(Classes.class.getPackage().getName() + "." + shortName);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }

        if (!type.isEnum())
            throw new NoSucheEntityException(shortName, Enum.class);

        return (Class)type;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<? extends ClientStorageStructure> requestClientStorage(final String fullName) throws NoSucheEntityException
    {
        final Class<?> type;
        try
        {
            type = requestClass(fullName);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }

        if (!ClientStorageStructure.class.isAssignableFrom(type))
            throw new NoSucheEntityException(fullName, ClientStorageStructure.class);

        return (Class)type;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<? extends ServerStorageStructure> requestServerStorage(final String fullName) throws NoSucheEntityException
    {
        final Class<?> type;
        try
        {
            type = requestClass(fullName);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }

        if (!ServerStorageStructure.class.isAssignableFrom(type))
            throw new NoSucheEntityException(fullName, ClientStorageStructure.class);

        return (Class)type;
    }
}
