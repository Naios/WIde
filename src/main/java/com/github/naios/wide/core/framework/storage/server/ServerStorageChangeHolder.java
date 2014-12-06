package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueHistory;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.storage.server.helper.StructureState;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.Pair;

public class ServerStorageChangeHolder implements Observable
{
    private static final ServerStorageChangeHolder INSTANCE = new ServerStorageChangeHolder();

    private final Map<ObservableValueStorageInfo, ObservableValue<?>> reference =
            new HashMap<>();

    private final Map<ObservableValue<?>, ObservableValueHistory> history =
            new IdentityHashMap<>();

    private final Set<InvalidationListener> listeners = new HashSet<>();

    /**
     * Inserts a new changed value into the history
     */
    public void insert(final ObservableValueStorageInfo storage, final ObservableValue<?> observable, final Object oldValue)
    {
        pushOnHistory(storage, observable, oldValue);
    }

    /**
     * Marks a ServerStorageStructure as just created
     */
    public void create(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> entry : storage)
            pushOnHistory(new ObservableValueStorageInfo(storage, entry.second()), entry.first(), StructureState.STATE_CREATED);
    }

    /**
     * Marks a ServerStorageStructure as just deleted
     */
    public void delete(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> entry : storage)
            pushOnHistory(new ObservableValueStorageInfo(storage, entry.second()), entry.first(), StructureState.STATE_DELETED);
    }

    /**
     * Cleans up the history to the last database sync
     */
    public void free()
    {
        for (final ObservableValueHistory history : history.values())
        {
            int idx = history.getHistory().indexOf(StructureState.STATE_IN_SYNC);
            if (idx == -1)
                continue;

            while (0 < idx--)
                history.getHistory().remove(0);

            if (history.getHistory().size() <= 1)
                removeFromHistory(reference.get(history.getReference()));
        }
    }

    /**
     * Updates the current database sync of all history stacks to now
     */
    public void update()
    {
        for (final ObservableValueHistory h : history.values())
        {
            h.getHistory().remove(StructureState.STATE_IN_SYNC);
            h.getHistory().push(StructureState.STATE_IN_SYNC);
        }
    }

    /**
     * Force removes all references of the observable from the holder
     * You need to check if there are references before you call this method!
     */
    private void removeFromHistory(final ObservableValue<?> observable)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        reference.remove(valueHistory.getReference());
        history.remove(observable);

        informListeners();
    }

    /**
     * Pushs an object to the history
     */
    public void pushOnHistory(final ObservableValueStorageInfo storage, final ObservableValue<?> observable,
            final Object oldValue)
    {
        ObservableValue<?> value = reference.get(storage);
        if (value == null)
        {
            reference.put(storage, observable);
            value = observable;
        }
        else
            assert(value == observable);

        ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
        {
            valueHistory = new ObservableValueHistory(storage);
            history.put(value, valueHistory);
        }
        if (valueHistory.validateNext())
            valueHistory.getHistory().push(oldValue);

        informListeners();
    }

    /**
     * Reverts all changes until the point you started the application
     * @param observable The Observable value you want to edit.
     */
    public void revert(final ObservableValue<?> observable)
    {
        rollback_impl(observable, -1, false);
    }

    /**
     * Drops all changes, so you are in sync with the database
     * @param observable The Observable value you want to edit.
     */
    public void drop(final ObservableValue<?> observable)
    {
        rollback_impl(observable, -1, true);
    }

    /**
     * Reverts the last change made
     * @param observable The Observable value you want to edit.
     */
    public void rollback(final ObservableValue<?> observable)
    {
        rollback_impl(observable, 1, false);
    }

    /**
     * Rolls {@link times} operations back.
     * @param observable The Observable value you want to edit.
     * @param times How many operations you want to roll back.
     */
    public void rollback(final ObservableValue<?> observable, final int times)
    {
        rollback_impl(observable, times, false);
    }

    private void rollback_impl(final ObservableValue<?> observable, int times, final boolean toCurrentSync)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null)
            return;

        if (valueHistory.getHistory().empty())
            removeFromHistory(observable);

        while ((0 != times--) && (!valueHistory.getHistory().empty()))
        {
            final Object value = valueHistory.getHistory().pop();
            if (value.equals(StructureState.STATE_IN_SYNC))
            {
                if (toCurrentSync)
                    break;
                else
                    continue;
            }
            else if (value.equals(StructureState.STATE_CREATED) ||
                     value.equals(StructureState.STATE_DELETED))
            {
                // TODO
            }

            // Prevents recursive calls
            valueHistory.invalidate();

            if (!ServerStorageFieldType.set(observable, value))
                valueHistory.validateNext();
        }

        // If the history is empty remove the observable from the history
        if (valueHistory.getHistory().empty())
            removeFromHistory(observable);
        else
            informListeners();
    }

    public void clear()
    {
        reference.clear();
        history.clear();
    }

    public static ServerStorageChangeHolder instance()
    {
        return INSTANCE;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s Observables were changed.", reference.size()));

        for (final Entry<ObservableValueStorageInfo, ObservableValue<?>> entry : reference.entrySet())
        {
            builder.append(String.format("\n%-17s (%s) ", entry.getKey().getTableName(), entry.getKey().getField().getName()));

            final Stack<Object> stack = history.get(entry.getValue()).getHistory();

            for (final Object obj : stack)
                builder.append(String.format("%s -> ", new FormatterWrapper(obj)));

            builder.append(String.format("Now: %s", new FormatterWrapper(entry.getValue().getValue())));
        }

        return builder.toString();
    }

    /**
     * @return All Observables that have changed since the last sync
     */
    public Collection<ObservableValue<?>> getObservablesChanged()
    {
        return getObservablesChanged(true);
    }

    public Collection<ObservableValue<?>> getObservablesChanged(final boolean sinceLastSync)
    {
        // Do we want to skip the last sync check?
        if (!sinceLastSync)
            return reference.values();

        final Collection<ObservableValue<?>> set = new HashSet<>();
        for (final Entry<ObservableValue<?>, ObservableValueHistory> entry : history.entrySet())
        {
            final int current_sync_pos = entry.getValue().getHistory().indexOf(StructureState.STATE_IN_SYNC);
            final int current_size = entry.getValue().getHistory().size();
            if (current_sync_pos < (current_size - 1))
                set.add(entry.getKey());
        }

        return set;
    }

    /**
     * @return The ObservableValueStorageInfo of an observable stored in the Changeholder
     */
    public ObservableValueStorageInfo getStorageInformationOfObservable(final ObservableValue<?> observable)
    {
        final ObservableValueHistory h = history.get(observable);
        if (h != null)
            return h.getReference();
        else
            return null;
    }

    private void informListeners()
    {
        for (final InvalidationListener listener : listeners)
            listener.invalidated(this);
    }

    @Override
    public void addListener(final InvalidationListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final InvalidationListener listener)
    {
        listeners.remove(listener);
    }
}
