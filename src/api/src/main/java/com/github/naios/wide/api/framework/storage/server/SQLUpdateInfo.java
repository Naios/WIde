
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;

public interface SQLUpdateInfo
{
    /**
     * @return Returns the property which was changed.
     */
    public ReadOnlyProperty<?> getProperty();

    /**
     * @return Returns an object property that hold the old value on remote if any.
     */
    public ObjectProperty<Optional<Object>> oldValueProperty();
}
