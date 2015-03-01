package com.github.naios.wide.api.util;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

import java.util.Iterator;
import java.util.function.Function;

public class CrossIterable<IN, OUT>
    extends AbstractCrossConvert<IN, OUT>
    implements Iterable<OUT>
{
    private final Iterable<IN> iterable;

    public CrossIterable(final Iterable<IN> iterable, final Function<IN, OUT> convert)
    {
        super(convert);
        this.iterable = iterable;
    }

    @Override
    public Iterator<OUT> iterator()
    {
        return new CrossIterator<>(iterable, getConvert());
    }
}
