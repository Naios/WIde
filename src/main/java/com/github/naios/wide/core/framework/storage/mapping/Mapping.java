
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping;

import java.util.List;
import java.util.Map.Entry;

public interface Mapping<BASE> extends Iterable<Entry<BASE, MappingMetadata>>
{
    public List<Entry<BASE, MappingMetadata>> getKeys();

    public List<Entry<BASE, MappingMetadata>> getValues();

    public void setDefaultValues();
}
