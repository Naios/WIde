
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.util;

public class Pair<F, S>
{
    private final F first;

    private final S second;

    public Pair(final F first, final S second)
    {
        this.first = first;
        this.second = second;
    }

    public F first()
    {
        return first;
    }

    public S second()
    {
        return second;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings({ "rawtypes" })
        final Pair other = (Pair) obj;
        if (first == null)
        {
            if (other.first != null)
                return false;
        }
        else if (!first.equals(other.first))
            return false;
        if (second == null)
        {
            if (other.second != null)
                return false;
        }
        else if (!second.equals(other.second))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        final String fs = (first == null) ? null : first.toString();
        final String ss = (second == null) ? null : second.toString();

        return String.format("Pair(%s, %s)", fs, ss);
    }
}
