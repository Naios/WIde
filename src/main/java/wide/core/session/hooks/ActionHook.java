package wide.core.session.hooks;

import java.util.Collection;

import wide.core.WIde;

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
    public void addListener(HookListener listener)
    {
        listeners.put(listener.getType(), listener);
    }

    /**
     * Removes the given listener of the {@link ActionHook}
     *
     * @param listener The {@link HookListener} you want to remove.
     */
    public void removeListener(HookListener listener)
    {
        listeners.remove(listener.getType(), listener);
    }

    /**
     * Removes all listener of the given owner {@link ActionHook}
     *
     * @param owner Owners are defined in {@link #HookListener}.
     */
    public void removeListenersOf(Object owner)
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
    public void fire(Hook hook)
    {
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println("Firing hook: " + hook.name());

        final Collection<HookListener> hook_to_inform = listeners.get(hook);
        for (final HookListener listener : hook_to_inform)
            listener.informed();
    }
}
