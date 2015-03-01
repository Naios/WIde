
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.mapping;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Mapping<BASE> extends Iterable<BASE>
{
    public List<BASE> getKeys();

    public List<Object> getRawKeys();

    public List<Object> getRawValues();

    public List<BASE> getValues();

    public BASE getEntryByName(String name) throws UnknownMappingEntryException;

    public BASE getEntryByTarget(String name) throws UnknownMappingEntryException;

    default Stream<BASE> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<BASE> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }
}
