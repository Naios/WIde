
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

public enum StructureState
{
    /**
     * The structure is alive and usable.
     */
    STATE_ALIVE
    {
        @Override
        public final boolean isAlive()
        {
            return true;
        }
    },

    /**
     * The structure was deleted and is not useable anymore.<br>
     * <b>Further access will cause exceptions</b>
     */
    STATE_DELETED,

    /**
     * Is pushed on the history stack so we know if the value was created
     */
    STATE_REQUEST_RELEASE;

    public boolean isAlive()
    {
        return false;
    }
}
