
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.hooks;

public abstract class HookListener
{
    private final Hook hook;

    private final Object owner;

    public HookListener(final Hook hook, final Object owner)
    {
        this.hook = hook;
        this.owner = owner;
    }

    public Hook getType()
    {
        return hook;
    }

    public Object getOwner()
    {
        return owner;
    }

    public abstract void informed();
}
