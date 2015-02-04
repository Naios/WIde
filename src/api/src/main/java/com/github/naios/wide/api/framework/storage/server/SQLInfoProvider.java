
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.server;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.util.Pair;

public interface SQLInfoProvider
{
    /**
     *
     * @param structure
     * @param entry
     * @return
     */
    public String getScopeOfEntry(ServerStorageStructure structure, Pair<ObservableValue<?>, MappingMetaData> entry);

    /**
     *
     * @param structure
     * @return
     */
    public String getScopeOfStructure(ServerStorageStructure structure);

    /**
     * Returns a scope comment for the given scope
     * @return A scope comment (never null)
     */
    public String getCommentOfScope(String scope);

    /**
     * Gets the custom variable of the observable value
     * @return null if not existing, variable name otherwise
     */
    public String getCustomVariable(ServerStorageStructure structure, ObservableValue<?> observable);
}
