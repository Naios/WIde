
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

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.Bundle;

import com.github.naios.wide.api.entities.EntityService;
import com.github.naios.wide.api.entities.NoSucheEntityException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.FlagUtil;
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

    /**
     * Converts hex into int if necessary
     */
    private int valueConverter(final String value)
    {
        if (value.startsWith("0x"))
            return Integer.parseInt(value.substring(2), 16);
        else if (value.startsWith("0b"))
            return Integer.parseInt(value.substring(2), 1);
        else
            return Integer.valueOf(value);
    }

    @SuppressWarnings("rawtypes")
    @Descriptor("Shows an enum constant of the given enum")
    public List<String> enums(@Descriptor("The enum name (UnitFlags, UnitClass for example)") final String name,
            @Descriptor("The value you want to translate (in decimal or hex)") final String value)
    {
        final int val = valueConverter(value);
        final Class<? extends Enum> enumeration = requestEnum(name);
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

    @SuppressWarnings("rawtypes")
    @Descriptor("Shows all enum flags of the value and the given enum.")
    public List<String> flags(@Descriptor("The enum name (UnitFlags, UnitClass for example)") final String name,
            @Descriptor("The flags you want to show (in decimal or hex).") final String value)
    {
        final int val = valueConverter(value);
        final Class<? extends Enum> enumeration = requestEnum(name);
        final List<? extends Enum> flags = FlagUtil.getFlagList(enumeration, val);

        final List<String> result = new ArrayList<>();

        result.add(String.format("Value: %s = %s", val, StringUtil.asHex(val)));

        for (final Enum flag : flags)
            result.add(String.format("\n%-10s = %s", StringUtil.asHex(FlagUtil.createFlag(flag)), flag.name()));

        return result;
    }

    @Descriptor("Converts hex or bin values to decimal.")
    public int todec(final String value)
    {
        return valueConverter(value);
    }

    @Descriptor("Converts decimal or bin vaues to hex.")
    public String tohex(final String value)
    {
        return StringUtil.asHex(valueConverter(value));
    }

    @Descriptor("Converts decimal or hex values to bin.")
    public String tobin(final String value)
    {
        return StringUtil.asBin(valueConverter(value));
    }
}
