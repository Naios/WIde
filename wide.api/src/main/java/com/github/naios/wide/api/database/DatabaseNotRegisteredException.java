
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.database;

/**
 * Thrown by {@link DatabasePoolService} if a requested id wasn't registered
 */
public class DatabaseNotRegisteredException extends UncheckedSQLException
{
    private static final long serialVersionUID = -7045866019798230168L;

    public DatabaseNotRegisteredException(final String id)
    {
        super(String.format("Database Id: \"%s\" is not registered", id));
    }
}
