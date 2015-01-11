
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

public class RollbackFailedException extends Exception
{
    private static final long serialVersionUID = -5170678578086981335L;

    public RollbackFailedException()
    {
        super();
    }

    public RollbackFailedException(final String message)
    {
        super(message);
    }

    public RollbackFailedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public RollbackFailedException(final Throwable cause)
    {
        super(cause);
    }
}
