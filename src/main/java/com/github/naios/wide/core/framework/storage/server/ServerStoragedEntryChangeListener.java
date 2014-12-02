package com.github.naios.wide.core.framework.storage.server;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ServerStoragedEntryChangeListener implements ChangeListener<Object>
{
    // TODO Find out whether gc cleans up the cached ServerStorageStructure correctly
    private final WeakReference<ServerStorageStructure> record;

    private final Field field;

    protected ServerStoragedEntryChangeListener(final ServerStorageStructure record, final Field field)
    {
        this.record = new WeakReference<ServerStorageStructure>(record);
        this.field = field;
    }

    @Override
    public void changed(final ObservableValue<? extends Object> observable,
            final Object oldValue, final Object newValue)
    {
        record.get().valueChanged(field, observable, oldValue);
    }
}
