
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.helper;

public enum StructureState
{
    /**
     * The structure is in sync with the database
     */
    STATE_IN_SYNC,

    /**
     * The structure was updated, and is not in sync of the database anymore.
     */
    STATE_UPDATED,

    /**
     * The current state of the structure is unknown (caused by database connection lost).
     */
    STATE_UNKNOWN,

    /**
     * Is pushed on the history stack so we know if the value was created
     */
    STATE_CREATED,

    /**
     * The structure was deleted and is not useable anymore.<br>
     * <b>Further access will cause exceptions</b>
     */
    STATE_DELETED;

    public boolean isInSync()
    {
        return this.equals(STATE_IN_SYNC);
    }

    public boolean isAlive()
    {
        return !this.equals(STATE_DELETED);
    }
}
