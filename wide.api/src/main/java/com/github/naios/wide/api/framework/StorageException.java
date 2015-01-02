
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework;

@SuppressWarnings("serial")
public class StorageException extends IllegalStateException
{
    public StorageException(final String msg)
    {
        super(msg);
    }

    public StorageException(final String msg, final Throwable cause)
    {
        super(msg, cause);
    }
}
