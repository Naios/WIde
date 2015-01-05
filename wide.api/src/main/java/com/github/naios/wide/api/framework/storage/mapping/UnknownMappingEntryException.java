
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.mapping;

@SuppressWarnings("serial")
public class UnknownMappingEntryException extends RuntimeException
{
    public UnknownMappingEntryException(final String name)
    {
        super(String.format("Requested mapping entry %s not found!", name));
    }
}
