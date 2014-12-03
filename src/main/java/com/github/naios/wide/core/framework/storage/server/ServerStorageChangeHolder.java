package com.github.naios.wide.core.framework.storage.server;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.util.FormatterWrapper;

class ObservableValueHistory
{
    private final ObservableValueInStorage reference;

    private final Stack<Object> history = new Stack<>();

    // Prevents recursive calls from Rollbacks that inform changelisteners
    private boolean nextIsValid = true;

    public ObservableValueHistory(final ObservableValueInStorage reference)
    {
        this.reference = reference;
    }

    public ObservableValueInStorage getReference()
    {
        return reference;
    }

    public Stack<Object> getHistory()
    {
        return history;
    }

    public boolean validateNext()
    {
        final boolean cache = nextIsValid;
        nextIsValid = true;
        return cache;
    }

    public void invalidate()
    {
        nextIsValid = false;
    }
}

public class ServerStorageChangeHolder
{
    private final static Object CURRENT_STATE = new Object();

    private static final ServerStorageChangeHolder INSTANCE = new ServerStorageChangeHolder();

    private final Map<ObservableValueInStorage, ObservableValue<?>> reference =
            new HashMap<>();

    private final Map<ObservableValue<?>, ObservableValueHistory> history =
            new IdentityHashMap<>();

    public void insert(final ObservableValueInStorage storage, final ObservableValue<?> observable, final Object oldValue)
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
    }

    public void remove(final ObservableValue<?> observable)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        reference.remove(valueHistory.getReference());
        history.remove(observable);
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

    public void rollback_impl(final ObservableValue<?> observable, int times, final boolean toCurrentSync)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null)
            return;

        if (valueHistory.getHistory().empty())
            remove(observable);

        while ((0 != times--) && (!valueHistory.getHistory().empty()))
        {
            final Object value = valueHistory.getHistory().pop();
            if (value == CURRENT_STATE)
                if (toCurrentSync)
                    break;
                else
                    continue;

            // Prevents recursive calls
            valueHistory.invalidate();

            if (observable instanceof IntegerProperty)
                ((IntegerProperty) observable).set((int) value);
            else if (observable instanceof BooleanProperty)
                ((BooleanProperty) observable).set((boolean) value);
            else if (observable instanceof FloatProperty)
                ((FloatProperty) observable).set((float) value);
            else if (observable instanceof DoubleProperty)
                ((DoubleProperty) observable).set((double) value);
            else if (observable instanceof StringProperty)
                ((StringProperty) observable).set((String) value);
        }

        // If the history is empty remove the observable from the history
        if (valueHistory.getHistory().empty())
            remove(observable);
    }

    public void clear()
    {
        reference.clear();
        history.clear();
    }

    public static ServerStorageChangeHolder Instance()
    {
        return INSTANCE;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s Observables were changed.\n", reference.size()));

        for (final Entry<ObservableValueInStorage, ObservableValue<?>> entry : reference.entrySet())
        {
            builder.append(String.format("%-17s (%s) ", entry.getKey().getTableName(), entry.getKey().getField().getName()));

            final Stack<Object> stack = history.get(entry.getValue()).getHistory();

            for (final Object obj : stack)
                builder.append(String.format("%s -> ", new FormatterWrapper(obj)));

            builder.append(String.format("Now: %s\n", new FormatterWrapper(entry.getValue().getValue())));
        }

        return builder.toString();
    }
}
