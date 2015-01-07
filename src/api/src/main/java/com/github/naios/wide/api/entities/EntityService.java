
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.entities;

import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;

public interface EntityService
{
    public Class<?> requestClass(final String fullName) throws NoSucheEntityException;

    @SuppressWarnings("rawtypes")
    public Class<? extends Enum> requestEnumForName(final String shortName) throws NoSucheEntityException;

    @SuppressWarnings("rawtypes")
    public Class<? extends Enum> requestEnum(final String fullName) throws NoSucheEntityException;

    public Class<? extends ClientStorageStructure> requestClientStorage(final String fullName) throws NoSucheEntityException;

    public Class<? extends ServerStorageStructure> requestServerStorage(final String fullName) throws NoSucheEntityException;
}
