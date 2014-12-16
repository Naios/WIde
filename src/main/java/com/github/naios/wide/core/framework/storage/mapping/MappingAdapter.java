
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

public interface MappingAdapter<FROM, BASE>
{
    public BASE create();

    public BASE map(FROM from, MappingMetadata metaData, BASE base);

    public void setDefault(BASE value);
}