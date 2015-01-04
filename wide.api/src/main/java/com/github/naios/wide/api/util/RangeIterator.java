
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

import java.util.Iterator;

import javafx.beans.property.SimpleIntegerProperty;

@Deprecated
public class RangeIterator<T extends Number> implements Iterator<T>
{
    SimpleIntegerProperty s;

    @SuppressWarnings("serial")
    private final static Number DEFAULT_STEP = new Number()
    {
        @Override
        public int intValue()
        {
            return 1;
        }

        @Override
        public long longValue()
        {
            return 1L;
        }

        @Override
        public float floatValue()
        {
            return 1.f;
        }

        @Override
        public double doubleValue()
        {
            return 1.d;
        }
    };

    @SuppressWarnings("serial")
    private final static Number DEFAULT_BEGIN = new Number()
    {
        @Override
        public int intValue()
        {
            return 0;
        }

        @Override
        public long longValue()
        {
            return 0L;
        }

        @Override
        public float floatValue()
        {
            return 0.f;
        }

        @Override
        public double doubleValue()
        {
            return 0.d;
        }
    };

    private Number begin;

    private final Number end, step;

    public RangeIterator(final T end)
    {
        this.begin = DEFAULT_BEGIN;
        this.end = end;
        this.step = DEFAULT_STEP;
    }

    public RangeIterator(final T begin, final T end)
    {
        this.begin = begin;
        this.end = end;
        this.step = DEFAULT_STEP;
    }

    public RangeIterator(final T begin, final T end, final T step)
    {
        this.begin = begin;
        this.end = end;
        this.step = step;
    }

    @Override
    public boolean hasNext()
    {
        return begin.doubleValue() < end.doubleValue();
    }

    @Override
    public T next()
    {
        begin = step.doubleValue() + begin.doubleValue();
        return null;
    }
}
