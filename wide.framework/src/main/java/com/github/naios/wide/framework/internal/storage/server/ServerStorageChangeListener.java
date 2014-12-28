
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.framework.internal.storage.server.helper.ObservableValueStorageInfo;

public class ServerStorageChangeListener implements ChangeListener<Object>
{
    private final ObservableValueStorageInfo info;

    protected ServerStorageChangeListener(final ServerStorageStructure storage, final String name)
    {
        this.info = new ObservableValueStorageInfo(storage, name);
    }

    @Override
    public void changed(final ObservableValue<?> observable,
            final Object oldValue, final Object newValue)
    {
        info.getStructure().getOwner().onValueChanged(info, observable, oldValue);
    }
}
