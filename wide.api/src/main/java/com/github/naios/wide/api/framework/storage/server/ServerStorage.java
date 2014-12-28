/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import java.util.List;

public interface ServerStorage<T extends ServerStorageStructure> extends AutoCloseable
{
    public String getTableName();

    public String getDatabaseId();

    public boolean isOpen();

    public T get(ServerStorageKey<T> key);

    public List<T> getWhere(String where, Object... args);

    public List<T> getWhere(String where);

    public T create(ServerStorageKey<T> key);

    @Override
    public void close();

}
