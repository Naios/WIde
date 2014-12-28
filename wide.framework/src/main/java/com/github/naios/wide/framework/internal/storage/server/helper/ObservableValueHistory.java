
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.helper;

import java.util.Stack;

import com.github.naios.wide.api.framework.storage.server.StructureState;
import com.github.naios.wide.framework.internal.storage.server.ServerStorageChangeHolder;

public class ObservableValueHistory
{
    private final ObservableValueStorageInfo reference;

    private final Stack<Object> history = new Stack<>();

    private String scope = ServerStorageChangeHolder.DEFAULT_SCOPE;

    private String customVariable = ServerStorageChangeHolder.DEFAULT_VARIABLE;

    /**
     *  Prevents recursive calls from Rollbacks that inform changelisteners
     */
    private boolean nextIsValid = true;

    public ObservableValueHistory(final ObservableValueStorageInfo reference)
    {
        this.reference = reference;
    }

    public ObservableValueStorageInfo getReference()
    {
        return reference;
    }

    public Stack<Object> getHistory()
    {
        return history;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(final String scope)
    {
        this.scope = scope;
    }

    public String getCustomVariable()
    {
        return customVariable;
    }

    public void setCustomVariable(final String customVariable)
    {
        this.customVariable = customVariable;
    }

    public boolean validateNext()
    {
        final boolean cache = nextIsValid;
        nextIsValid = true;
        return cache;
    }

    public void invalidate()
    {
        nextIsValid = false;
    }

    public boolean empty()
    {
        return history.size() <= ((history.contains(StructureState.STATE_IN_SYNC)) ? 1 : 0);
    }
}
