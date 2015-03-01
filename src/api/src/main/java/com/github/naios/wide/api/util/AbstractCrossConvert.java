package com.github.naios.wide.api.util;

/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */



import java.util.function.Function;

public abstract class AbstractCrossConvert<IN, OUT>
{
    private final Function<IN, OUT> convert;

    public AbstractCrossConvert(final Function<IN, OUT> convert)
    {
        this.convert = convert;
    }

    public Function<IN, OUT> getConvert()
    {
        return convert;
    }

    protected OUT convert(final IN in)
    {
        return convert.apply(in);
    }
}
