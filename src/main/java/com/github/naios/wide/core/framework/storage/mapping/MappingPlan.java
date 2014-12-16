
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.List;

import com.google.common.reflect.TypeToken;

public interface MappingPlan
{
    public int getNumberOfElements();

    public List<Integer> getKeys();

    public int getNumberOfKeys();

    public List<MappingMetaData> getMetadata();

    public String getNameOfOrdinal(final int ordinal);

    public int getOrdinalOfName(final String name) throws OrdinalNotFoundException;

    public List<TypeToken<?>> getMappedType();
}
