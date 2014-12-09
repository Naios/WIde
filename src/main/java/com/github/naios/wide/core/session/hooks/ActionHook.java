
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.hooks;

import java.util.Collection;

import com.github.naios.wide.core.WIde;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * {@link ActionHook} implements a global listener informer.<p>
 * {@link HookListener} are added with {@link #addListener(HookListener listener)}
 * and removed with {@link #removeListener(HookListener listener)}.<p>
 * Call your registered listeners with {@link #fire(Hook hook)}.
 */
public class ActionHook
{
    private final Multimap<Hook, HookListener> listeners = HashMultimap.create();

    /**
     * Adds the given listener to the {@link ActionHook}
     *
     * @param listener The {@link HookListener} you want to add.
     */
    public void addListener(final HookListener listener)
    {
        listeners.put(listener.getType(), listener);
    }

    /**
     * Removes the given listener of the {@link ActionHook}
     *
     * @param listener The {@link HookListener} you want to remove.
     */
    public void removeListener(final HookListener listener)
    {
        listeners.remove(listener.getType(), listener);
    }

    /**
     * Removes all listener of the given owner {@link ActionHook}
     *
     * @param owner Owners are defined in {@link #HookListener}.
     */
    public void removeListenersOf(final Object owner)
    {
        final Collection<HookListener> all_listeners = listeners.values();
        for (final HookListener listener : all_listeners)
            if (listener.getOwner() == owner)
                listeners.remove(listener.getType(), listener);
    }

    /**
     * Informs all {@link HookListener}s of the given {@link Hook}.
     *
     * @param hook {@link Hook} you want to inform about.
     */
    public void fire(final Hook hook)
    {
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println("Firing hook: " + hook.name());

        final Collection<HookListener> hook_to_inform = listeners.get(hook);
        for (final HookListener listener : hook_to_inform)
            listener.informed();
    }
}
