
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool;

import java.sql.SQLException;

/**
 * An unchecked exception type to wrap {@link java.sql.SQLException}'s
 */
public class UncheckedSQLException extends RuntimeException
{
    private static final long serialVersionUID = 2791696355518638817L;

    public UncheckedSQLException(final SQLException exception)
    {
        super(exception);
    }
}
