
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.server.builder.SQLBuilder;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueHistory;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.storage.server.helper.StructureState;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.IdentitySet;
import com.github.naios.wide.core.framework.util.Pair;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.core.session.database.DatabaseType;

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
    private final ObjectProperty<Connection> connection =
            new SimpleObjectProperty<>();

    private final Map<ObservableValueStorageInfo, ObservableValue<?>> reference =
            new HashMap<>();

    private final Map<ObservableValue<?>, ObservableValueHistory> history =
            new IdentityHashMap<>();

    private final Set<InvalidationListener> listeners =
            new HashSet<>();

    private final Set<ServerStorageStructure> reBuild =
            new IdentitySet<>();

    private final static int TIMES_UNLIMITED = -1;

    public final static String DEFAULT_SCOPE = "";

    public final static String DEFAULT_SCOPE_COMMENT = "";

    public final static String DEFAULT_VARIABLE = null;

    private final StringProperty scope =
            new SimpleStringProperty(DEFAULT_SCOPE);

    private final Map<String, String> scopeComments =
            new HashMap<>();

    protected ServerStorageChangeHolder(final DatabaseType databaseType)
    {
        this.connection.bind(WIde.getDatabase().connection(databaseType));
        this.connection.addListener(new ChangeListener<Connection>()
        {
            @Override
            public void changed(
                    final ObservableValue<? extends Connection> observable,
                    final Connection oldValue, final Connection newValue)
            {
                // if the connection change invalidate all changes
                invalidate();
            }
        });
    }

    /**
     * @return Our scope property
     */
    public StringProperty scope()
    {
        return scope;
    }

    /**
     * Sets our current scope
     * @param scope unique Scope identifier
     */
    public void setScope(final String scope)
    {
        this.scope.set(scope);
    }

    /**
     * Sets our current scope
     * @param scope unique Scope identifier
     * @param comment our comment we want to set
     */
    public void setScope(final String scope, final String comment)
    {
        scopeComments.put(scope, comment);
        setScope(scope);
    }

    /**
     * Releases the scope<br>
     * Equal to setScope(DEFAULT_SCOPE)
     */
    public void releaseScope()
    {
        scope.set(DEFAULT_SCOPE);
    }

    /**
     * Returns a scope comment for the given scope
     * @return A scope comment (never null)
     */
    public String getScopeComment(final String scope)
    {
        return scopeComments.getOrDefault(scope, DEFAULT_SCOPE_COMMENT);
    }

    /**
     * Sets an observable value as custom variable<br>
     * Value is wrapped into the variable then
     */
    public void setCustomVariable(final ObservableValue<?> value, final String name)
    {
        final ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
            return;

        valueHistory.setCustomVariable(name);
    }

    /**
     * Releases a custom variable of an observable value
     */
    public void releaseCustomVariable(final ObservableValue<?> value)
    {
        final ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
            return;

        valueHistory.setCustomVariable(DEFAULT_VARIABLE);
    }

    /**
     * Gets the custom variable of the observable value
     * @param value The observable value we want to get the variable name of
     * @return null if not existing, variable name otherwise
     */
    public String getCustomVariable(final ObservableValue<?> value)
    {
        final ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
            return null;

        return valueHistory.getCustomVariable();
    }

    /**
     * Sets the comment of the current scope
     * @param comment the comment you want to set
     */
    public void setScopeComment(final String comment)
    {
        scopeComments.put(scope.get(), comment);
    }

    /**
     * Gets our value that is currently present in the origin
     * @param value we want to get the state of
     * @return the state that is present at the origin
     */
    public Object getValueAtOrigin(final ObservableValue<?> value)
    {
        final ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
            return null;

        final int idx = valueHistory.getHistory().indexOf(StructureState.STATE_IN_SYNC);
        if(idx == -1)
            return valueHistory.getHistory().firstElement();
        else
            return valueHistory.getHistory().get(idx - 1);
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
            if (ServerStorageFieldType.isValueWriteable(entry.first()))
                pushOnHistory(new ObservableValueStorageInfo(storage, entry.second()), entry.first(), StructureState.STATE_CREATED);
    }

    /**
     * Marks a ServerStorageStructure as just deleted
     */
    protected void delete(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> entry : storage)
            if (ServerStorageFieldType.isValueWriteable(entry.first()))
            {
                final ObservableValueStorageInfo info = new ObservableValueStorageInfo(storage, entry.second());
                insert(info, entry.first(), entry.first().getValue());
                insert(info, entry.first(), StructureState.STATE_DELETED);
            }
    }

    /**
     * Resets the structure, sets all non key values to its default values
     */
    protected void reset(final ServerStorageStructure storage)
    {
        for (final Pair<ObservableValue<?>, Field> value : storage)
            setDefault(value.first());
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
                erase(reference.get(history.getReference()));
        }
    }

    /**
     * Clears the history
     */
    public void clear()
    {
        for (final ObservableValueStorageInfo history : reference.keySet())
            history.getStructure().writeableState().set(StructureState.STATE_IN_SYNC);

        reference.clear();
        history.clear();
        releaseScope();
        scopeComments.clear();
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

        for (final ObservableValueStorageInfo structure : reference.keySet())
            structure.getStructure().writeableState().set(StructureState.STATE_IN_SYNC);
    }

    /**
     * Invalidates the sync state
     */
    protected void invalidate()
    {
        for (final ObservableValueHistory h : history.values())
            h.getHistory().remove(StructureState.STATE_IN_SYNC);

        for (final ObservableValueStorageInfo structure : reference.keySet())
            structure.getStructure().writeableState().set(StructureState.STATE_UNKNOWN);
    }

    /**
     * Force removes all references of the observable from the holder
     * You need to check if there are references before you call this method!
     */
    private void erase(final ObservableValue<?> observable)
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

        valueHistory.setScope(scope.get());

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
    public void drop(final ServerStorageStructure structure)
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
            erase(observable);
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

        final Set<ServerStorageStructure> touched =
                new IdentitySet<>();

        final Set<ServerStorageStructure> delete =
                new IdentitySet<>();

        for (final Entry<ObservableValue<?>, ObservableValueHistory> entry : localHistory.entrySet())
        {
            final Stack<Object> stack = entry.getValue().getHistory();

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
                if (touched.contains(entry.getValue().getReference().getStructure()))
                    return;
                else
                    throw new MalformedHistoryException();

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

            // if we are behind the current sync state, rebuild the record in the database
            if (revertIDX < stack.indexOf(StructureState.STATE_IN_SYNC))
                reBuild.add(entry.getValue().getReference().getStructure());

            // Roll value back
            stack.setSize(revertIDX); // roll 1 step behind idx
            stack.trimToSize();

            // If the stack is empty the structure was create in the current session, delete it
            if (stack.isEmpty())
            {
                delete.add(entry.getValue().getReference().getStructure());
                setDefault(entry.getKey());
            }
            else
                set(entry.getKey(), stack.pop());

            // Structure is touched (first checked)
            touched.add(entry.getValue().getReference().getStructure());

            if (entry.getValue().empty())
                erase(entry.getKey());
        }

        delete.forEach((struc) -> struc.writeableState().set(StructureState.STATE_DELETED));
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

    private void setDefault(final ObservableValue<?> observable)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null)
            return;

        // Prevents recursive calls
        valueHistory.invalidate();
        ServerStorageFieldType.setDefault(observable);
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

            builder.append(StringUtil.concat(" -> ", getHistory(entry.getValue())));

            builder.append(String.format(" -> Now: %s", new FormatterWrapper(entry.getValue().getValue())));
        }

        return builder.toString();
    }

    /**
     * @return Returns the latest known state of an observable value
     */
    public StructureState getObservablesLatestState(final ObservableValue<?> observable)
    {
        final ObservableValueHistory h = history.get(observable);
        if (h != null)
        {
            final ListIterator<Object> i = h.getHistory().listIterator(h.getHistory().size());
            while (i.hasPrevious())
            {
                final Object cur = i.previous();
                if (cur instanceof StructureState)
                    return (StructureState) cur;
            }
        }
        return null;
    }

    /**
     * @return All Observables that have changed
     */
    public Collection<ObservableValue<?>> getAllObservablesChanged()
    {
        final Collection<ObservableValue<?>> col = new ArrayList<>(reference.values());
        col.removeIf((entry) ->
        {
            return StructureState.STATE_DELETED.equals(getObservablesLatestState(entry));
        });

        return col;
    }

    /**
     * @return All Observables that have changed since the last sync
     */
    public Collection<ObservableValue<?>> getObservablesChanged()
    {
        final Collection<ObservableValue<?>> col = getAllObservablesChanged();
        col.removeIf((entry) ->
        {
            final ObservableValueHistory h = history.get(entry);
            if (h == null)
                return true;

            return StructureState.STATE_IN_SYNC.equals(getObservablesLatestState(entry)) &&
                    (h.getHistory().indexOf(StructureState.STATE_IN_SYNC) != (h.getHistory().size() - 1));
        });
        return col;
    }

    /**
     * @return All Structures that have changed
     */
    public Collection<ServerStorageStructure> getAllStructuresChanged()
    {
        final Collection<ServerStorageStructure> col = new HashSet<>();
        reference.keySet().forEach(entry -> col.add(entry.getStructure()));
        return col;
    }

    /**
     * @return All Structures recently created (since the last database sync)
     */
    public Collection<ServerStorageStructure> getStructuresRecentlyCreated()
    {
        final Collection<ObservableValue<?>> observables = getAllObservablesChanged();
        final Collection<ServerStorageStructure> structures = new IdentitySet<>();

        observables.forEach((entry) ->
        {
            final ObservableValueHistory h = history.get(entry);
            if (h == null)
                return;

            if (StructureState.STATE_CREATED.equals(getObservablesLatestState(entry)))
                structures.add(h.getReference().getStructure());
        });

        return structures;
    }

    /**
     * @return All Structures recently deleted (since the last database sync)
     */
    public Collection<ServerStorageStructure> getStructuresRecentlyDeleted()
    {
        final Collection<Entry<ObservableValue<?>, ObservableValueHistory>> observables = history.entrySet();
        final Collection<ServerStorageStructure> structures = new IdentitySet<>();

        observables.forEach((entry) ->
        {
            if (entry.getValue().getHistory().isEmpty())
                return;

            if (StructureState.STATE_DELETED.equals(entry.getValue().getHistory().peek()))
                structures.add(entry.getValue().getReference().getStructure());
        });

        return structures;
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

    public String getScopeOfObservable(final ObservableValue<?> observable)
    {
        final ObservableValueHistory h = history.get(observable);
        if (h != null)
            return h.getScope();
        else
            return "";
    }

    public String getScopeOfStructure(final ServerStorageStructure structure)
    {
        // Try to get 1 scope of any observable value.
        for (final Pair<ObservableValue<?>, Field> entry : structure)
        {
            final String scope = getScopeOfObservable(entry.first());
            if (!scope.isEmpty())
                return scope;
        }

        return "";
    }

    /**
     * Commits all changes to the database
     */
    public void commit()
    {
        update();
    }

    public String getQuery()
    {
        final SQLBuilder builder = new SQLBuilder(this, true);

        builder.addRecentChanged();
        builder.addCreate(getStructuresRecentlyCreated());
        builder.addDelete(getStructuresRecentlyDeleted());

        return builder.toString();
    }

    /**
     * Returns the whole history of an ObservableValue
     * @param value The ObservableValue you want to get the history
     * @return An array containing all versioned objects.
     */
    public Object[] getHistory(final ObservableValue<?> value)
    {
        final ObservableValueHistory valueHistory = history.get(value);
        if (valueHistory == null)
            return null;

        return valueHistory.getHistory().toArray();
    }

    public ObjectProperty<Connection> connection()
    {
        return connection;
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
