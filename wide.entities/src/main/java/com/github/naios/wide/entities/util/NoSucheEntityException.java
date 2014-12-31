
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.util;

@SuppressWarnings("serial")
public class NoSucheEntityException extends RuntimeException
{
    public NoSucheEntityException(final String name, final Throwable cause)
    {
        super(String.format("Didn't find Entity %s!", name), cause);
    }

    public NoSucheEntityException(final String name, final Class<?> requiredType)
    {
        super(String.format("Entity %s is not a %s!", name, requiredType.getName()));
    }
}
