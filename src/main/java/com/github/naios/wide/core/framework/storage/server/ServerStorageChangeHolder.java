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
import com.github.naios.wide.core.framework.util.IdentitySet;
import com.github.naios.wide.core.framework.util.Pair;

@SuppressWarnings("serial")
class MalformedHistoryException extends IllegalStateException
{
    public MalformedHistoryException()
    {
        super("ServerStorageChangeHolder history seems to be corrupted!");
    }
}

public class ServerStorageChangeHolder implements Observable
{
    private static final ServerStorageChangeHolder INSTANCE = new ServerStorageChangeHolder();

    private final Map<ObservableValueStorageInfo, ObservableValue<?>> reference =
            new HashMap<>();

    private final Map<ObservableValue<?>, ObservableValueHistory> history =
            new IdentityHashMap<>();

    private final Set<InvalidationListener> listeners =
            new HashSet<>();

    private final Set<ServerStorageStructure> reBuild =
            new IdentitySet<>();

    private final static int TIMES_UNLIMITED = -1;

    /**
     * @return The global ServerStorageChangeHolder instance.
     */
    public static ServerStorageChangeHolder instance()
    {
        return INSTANCE;
    }

    /**
     * Inserts a new changed value into the history
     */
    protected void insert(final ObservableValueStorageInfo storage, final ObservableValue<?> observable, final Object oldValue)
    {
        pushOnHistory(storage, observable, oldValue);
    }

    /**
     * Marks a ServerStorageStructure as just created
     */
    protected void create(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> entry : storage)
            pushOnHistory(new ObservableValueStorageInfo(storage, entry.second()), entry.first(), StructureState.STATE_CREATED);
    }

    /**
     * Marks a ServerStorageStructure as just deleted
     */
    protected void delete(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> entry : storage)
        {
            final ObservableValueStorageInfo info = new ObservableValueStorageInfo(storage, entry.second());
            insert(info, entry.first(), entry.first().getValue());
            pushOnHistory(info, entry.first(), StructureState.STATE_DELETED);
        }
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

            if (history.empty())
                removeFromHistory(reference.get(history.getReference()));
        }
    }

    /**
     * Clears the history
     */
    public void clear()
    {
        for (final ObservableValueStorageInfo history : reference.keySet())
            history.getStructure().setState(StructureState.STATE_IN_SYNC);

        reference.clear();
        history.clear();
    }

    /**
     * Updates the current database sync of all history stacks to now
     */
    protected void update()
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
    private void pushOnHistory(final ObservableValueStorageInfo storage, final ObservableValue<?> observable,
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
     * Reverts structure <b>hard</b> to last state.<br>
     * Recovers deleted structures!<br>
     * <b>Will erase all changes made on the structure.</b>
     * @param observable value you want to edit.
     */
    public void revert(final ServerStorageStructure structure)
    {
        revertImplementation(structure, true);
    }

    /**
     * Resets all changes until the point you started the application
     * @param structure value you want to edit.
     */
    public void reset(final ServerStorageStructure structure)
    {
        revertImplementation(structure, false);
    }

    /**
     * Trys to reset all changes until the point you started the application
     * <b>may be unsuccessful if the structure was deleted & inserted</b>
     * @param structure value you want to edit.
     */
    public void tryReset(final ObservableValue<?> observable)
    {
        rollbackImplementation(observable, TIMES_UNLIMITED, false);
    }

    /**
     * Drop all changes, made since the last sync.<br>
     * @param observable value you want to edit.
     */
    public void drop(final ObservableValue<?> observable)
    {
        rollbackImplementation(observable, TIMES_UNLIMITED, true);
    }

    /**
     * Reverts the last change made
     * @param observable value you want to edit.
     */
    public void rollback(final ObservableValue<?> observable)
    {
        rollbackImplementation(observable, 1, false);
    }

    /**
     * Rolls {@link times} operations back.
     * You cant't roll back behind insert/deletes
     * @param observable The Observable value you want to edit.
     * @param times How many operations you want to roll back.
     */
    public void rollback(final ObservableValue<?> observable, final int times)
    {
        rollbackImplementation(observable, times, false);
    }

    /**
     * Rolls back the history<br>
     * <b>Can't roll back behind deletion/ insertion</b>
     * @param observable you want to roll back
     * @param times of steps you want to roll back
     * @param soft shall we only revert to the current database sync (drop changes made after the last sync)?
     */
    private void rollbackImplementation(final ObservableValue<?> observable, int times, final boolean soft)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null)
            return;

        while ((0 != times--) && (!valueHistory.empty()))
        {
            final Object value = valueHistory.getHistory().peek();
            if (value.equals(StructureState.STATE_IN_SYNC))
            {
                if (soft)
                    break;
                else
                {
                    valueHistory.getHistory().pop();
                    continue;
                }
            }
            else if (value.equals(StructureState.STATE_CREATED)
                    || value.equals(StructureState.STATE_DELETED))
                return;

            valueHistory.getHistory().pop();
            set(observable, value);
        }

        // If the history is empty remove the observable from the history
        if (valueHistory.empty())
            removeFromHistory(observable);
        else
            informListeners();
    }

    /**
     * Reverts the structure hard.
     * @param structure you want to revert.
     * @param once shall we only revert one state?
     */
    private void revertImplementation(final ServerStorageStructure structure, final boolean once)
    {
        // Get all observable values contained in the holder of the structure
        final Map<ObservableValue<?>, ObservableValueHistory> localHistory =
                new IdentityHashMap<>();

        reference.entrySet().forEach((entry) ->
        {
            if (entry.getKey().getStructure().equals(structure))
                localHistory.put(entry.getValue(), history.get(entry.getValue()));
        });

        if (localHistory.isEmpty())
            return;

        boolean touch = true;
        for (final Entry<ObservableValue<?>, ObservableValueHistory> entry : localHistory.entrySet())
        {
            final Stack<Object> stack = entry.getValue().getHistory();

            // If the structure wasn't re-created its not needed to continue
            if (touch)
            {
                if (!stack.contains(StructureState.STATE_CREATED) &&
                    !stack.contains(StructureState.STATE_DELETED))
                        return;

                touch = false;
            }

            final int createIDX, deleteIDX, revertIDX;
            if (once)
            {
                // If we want to revert to the latest state the state objects are at the end
                createIDX = stack.lastIndexOf(StructureState.STATE_CREATED);
                deleteIDX = stack.lastIndexOf(StructureState.STATE_DELETED);
            }
            else
            {
                // If we want to revert to the first state the state objects are in front
                createIDX = stack.indexOf(StructureState.STATE_CREATED);
                deleteIDX = stack.indexOf(StructureState.STATE_DELETED);
            }

            /* Delete && !Create -> Value was there before application started
             *
             * !Delete && Create -> Value is new
             *
             * Delete && Create -> Value was there, and deleted
             *
             * !Delete && !Create -> Stack corrupted!
             */

            if (createIDX == -1 && deleteIDX == -1)
                if (touch)
                    return;
                else
                    throw new MalformedHistoryException();

            if (touch)
                touch = false;

            // Select index to revert to
            if (createIDX == -1)
                revertIDX = deleteIDX;
            else if (deleteIDX == -1)
                revertIDX = createIDX;
            else
                if (once)
                    revertIDX = Math.max(createIDX, deleteIDX);
                else
                    revertIDX = Math.min(createIDX, deleteIDX);

            final boolean isBehindCurrentSync = revertIDX < stack.indexOf(StructureState.STATE_IN_SYNC);

            // Roll value back
            stack.setSize(revertIDX); // roll 1 step behind idx
            stack.trimToSize();

            // TODO if the structure was loaded and instantly deleted this logic will fail...

            final Object value = stack.peek();
            // If the structure was deleted the default value must be placed into the observable
            if (value.equals(StructureState.STATE_DELETED))
                ServerStorageFieldType.loadDefault(entry.getKey());
            // After a STATE_CREATED always follows the default value in the stack if it was deleted
            else if (value.equals(StructureState.STATE_CREATED))
                throw new MalformedHistoryException();
            else
                set(entry.getKey(), stack.pop());

            if (isBehindCurrentSync)
                reBuild.add(entry.getValue().getReference().getStructure());
        }
    }

    private void set(final ObservableValue<?> observable, final Object value)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null)
            return;

        // Prevents recursive calls
        valueHistory.invalidate();

        if (!ServerStorageFieldType.set(observable, value))
            valueHistory.validateNext();
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
     * @return All Observables that have changed
     */
    public Collection<ObservableValue<?>> getAllObservablesChanged()
    {
        return reference.values();
    }

    /**
     * @return All Observables that have changed since the last sync
     */
    public Collection<ObservableValue<?>> getObservablesChanged()
    {
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

    /**
     * Commits all changes to the database
     */
    public void commit()
    {


        update();
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
