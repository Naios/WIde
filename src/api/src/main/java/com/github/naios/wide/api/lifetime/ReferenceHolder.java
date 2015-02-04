
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.lifetime;

public interface ReferenceHolder<T extends Releaseable>
{
    /**
     * Releases the given Releaseable.
     *
     * @param releaseable The {@link Releaseable} you want to release.
     * @return Returns true on success.
     */
    public boolean release(T releaseable);
}
