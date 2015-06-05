
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal.controls.map;

import java.util.Set;

public interface WorldMapLoader
{
    /**
     *
     * @return
     */
    public Set<WorldMapNode> fetchNodes();
}
