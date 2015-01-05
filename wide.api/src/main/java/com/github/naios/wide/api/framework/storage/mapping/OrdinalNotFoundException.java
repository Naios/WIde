
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.mapping;

@SuppressWarnings("serial")
public class OrdinalNotFoundException extends Exception
{
    public OrdinalNotFoundException(final String name)
    {
        super(String.format("Name %s is not present, ordinal not found!", name));
    }
}
