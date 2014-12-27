
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.database_pool;

import java.sql.SQLException;

/**
 * Thrown by {@link DatabasePoolService} if a requested id wasn't registered
 */
public abstract class DatabaseNotRegisteredException extends SQLException
{
    private static final long serialVersionUID = -7045866019798230168L;
}
