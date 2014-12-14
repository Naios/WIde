
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Wrapper to convert iterators to specific output
 */
public class CrossIterator<IN, OUT> implements Iterator<OUT>
{
    private final Iterator<IN> iterator;

    private final Function<IN, OUT> convert;

    public CrossIterator(final Iterable<IN> iterable, final Function<IN, OUT> convert)
    {
        this (iterable.iterator(), convert);
    }

    public CrossIterator(final Iterator<IN> iterator, final Function<IN, OUT> convert)
    {
        this.iterator = iterator;

        this.convert = convert;
    }

    @Override
    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    @Override
    public OUT next()
    {
        return convert.apply(iterator.next());
    }
}
