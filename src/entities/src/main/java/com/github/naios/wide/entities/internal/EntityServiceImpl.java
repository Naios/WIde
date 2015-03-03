
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.Bundle;

import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.entities.NoSucheEntityException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.Flags;
import com.github.naios.wide.api.util.StringUtil;
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> requestClass(final String fullName) throws NoSucheEntityException
    {
        try
        {
            Class<T> type = (Class<T>)classes.get(fullName);

            if (type != null)
                return type;

            type = (Class<T>)bundle.loadClass(fullName);
            classes.put(fullName, type);
            return type;
        }
        catch (final ClassNotFoundException e)
        {
            throw new NoSucheEntityException(fullName, e);
        }
    }

    @Override
    public <T extends Enum<?>> Class<T> requestEnumForName(final String shortName) throws NoSucheEntityException
    {
        return requestEnum(Classes.class.getPackage().getName() + "." + shortName);
    }

    @Override
    public <T extends Enum<?>> Class<T> requestEnum(final String fullName)
            throws NoSucheEntityException
    {
        final Class<T> type;
        try
        {
            type = requestClass(fullName);
        }
        catch (final NoSucheEntityException e)
        {
            throw e;
        }

        if (!type.isEnum())
            throw new NoSucheEntityException(fullName, Enum.class);

        return type;
    }

    @Override
    public <T extends ClientStorageStructure> Class<T> requestClientStorage(final String fullName) throws NoSucheEntityException
    {
        final Class<T> type;
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

        return type;
    }

    @Override
    public <T extends ServerStorageStructure> Class<T> requestServerStorage(final String fullName) throws NoSucheEntityException
    {
        final Class<T> type;
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

        return type;
    }

    @SuppressWarnings("rawtypes")
    @Descriptor("Shows an enum constant of the given enum")
    public List<String> enums(@Descriptor("The enum name (UnitFlags, UnitClass for example)") final String name,
            @Descriptor("The value you want to translate (in decimal or hex)") final String value)
    {
        final int val = StringUtil.convertToInt(value);
        final Class<? extends Enum> enumeration = requestEnumForName(name);
        final List<String> result = new ArrayList<>();

        final Enum enumValue;
        try
        {
            enumValue = enumeration.getEnumConstants()[val];
        }
        catch (final Exception e)
        {
            throw new RuntimeException(String.format("Value %s is not a part of Enum %s.", val, name));
        }

        result.add(String.format("Value: %s = %s = %s", val, StringUtil.asHex(val), enumValue.name()));
        return result;
    }

    @Descriptor("Shows all enum flags of the value and the given enum.")
    public List<String> flags(@Descriptor("The enum name (UnitFlags, UnitClass for example)") final String name,
            @Descriptor("The flags you want to show (in decimal or hex).") final String value)
    {
        final int val = StringUtil.convertToInt(value);
        final Class<? extends Enum<?>> enumeration = requestEnumForName(name);
        final Set<? extends Enum<?>> flags = Flags.flagSet(enumeration, val);

        final List<String> result = new ArrayList<>();

        result.add(String.format("Value: %s = %s", val, StringUtil.asHex(val)));

        for (final Enum<?> flag : flags)
            result.add(String.format("\n%-10s = %s", StringUtil.asHex(Flags.createFlag(flag)), flag.name()));

        return result;
    }

    @Descriptor("Converts hex or bin values to decimal.")
    public int todec(final String value)
    {
        return StringUtil.convertToInt(value);
    }

    @Descriptor("Converts decimal or bin vaues to hex.")
    public String tohex(final String value)
    {
        return StringUtil.asHex(StringUtil.convertToInt(value));
    }

    @Descriptor("Converts decimal or hex values to bin.")
    public String tobin(final String value)
    {
        return StringUtil.asBin(StringUtil.convertToInt(value));
    }
}
