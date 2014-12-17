
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.lang.ref.WeakReference;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;

public class ServerStorageChangeListener implements ChangeListener<Object>
{
    // TODO Find out whether gc cleans up the cached ServerStorageStructure correctly
    private final WeakReference<ObservableValueStorageInfo> info;

    protected ServerStorageChangeListener(final ServerStorageStructure storage, final String name)
    {
        this.info = new WeakReference<>(new ObservableValueStorageInfo(storage, name));
    }

    @Override
    public void changed(final ObservableValue<?> observable,
            final Object oldValue, final Object newValue)
    {
        info.get().getStructure().getOwner().onValueChanged(info.get(), observable, oldValue);
    }
}
