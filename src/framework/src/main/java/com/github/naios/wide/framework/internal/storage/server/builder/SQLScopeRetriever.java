
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.Pair;

public interface SQLScopeRetriever
{
    String getScopeOfEntry(Pair<ObservableValue<?>, MappingMetaData> entry);

    String getScopeOfStructure(ServerStorageStructure structure);
}
