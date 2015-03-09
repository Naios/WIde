
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.mapping;

import java.util.List;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.OrdinalNotFoundException;
import com.google.common.reflect.TypeToken;

public interface MappingPlan<BASE>
{
    public int getNumberOfElements();

    public List<MappingMetaData> getKeys();

    public int getNumberOfKeys();

    public List<MappingMetaData> getMetaData();

    public String getNameOfOrdinal(final int ordinal);

    public int getOrdinalOfName(final String name) throws OrdinalNotFoundException;

    public int getOrdinalOfTarget(final String name) throws OrdinalNotFoundException;

    public List<TypeToken<? extends BASE>> getMappedTypes();
}
