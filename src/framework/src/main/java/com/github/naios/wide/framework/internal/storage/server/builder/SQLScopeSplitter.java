
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.Map;
import java.util.function.Consumer;

import com.github.naios.wide.framework.internal.storage.server.ServerStorageChangeHolderImpl;

/**
 * Helps us to split scopes of multiple collections
 */
public abstract class SQLScopeSplitter<T> implements Consumer<T>
{
    private final Map<String, SQLScope> scopes;

    protected SQLScopeSplitter(final ServerStorageChangeHolderImpl holder, final Map<String, SQLScope> scopes)
    {
        this.scopes = scopes;
    }

    @Override
    public void accept(final T entry)
    {
        final String name = getScope(entry);

        SQLScope scope = scopes.get(name);
        if (scope == null)
        {
            scope = new SQLScope();
            scopes.put(name, scope);
        }

        addObservable(scope, entry);
    }

    public abstract String getScope(T entry);

    public abstract void addObservable(SQLScope scope, T entry);
}
