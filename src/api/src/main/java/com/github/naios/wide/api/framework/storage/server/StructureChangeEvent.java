
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;


public interface StructureChangeEvent
{
    /**
     * Creates an anti change event if possible that reverts the version change.<br>
     * Takes care of FlagsProperties, creation and deletion of structures.
     */
    public void revert();

    /**
     * Tries to silent drop the change and removes it from the change history.
     * Not recommended for productive use.
     */
    public void drop();
}
