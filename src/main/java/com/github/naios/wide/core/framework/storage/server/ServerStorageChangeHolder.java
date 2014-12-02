package com.github.naios.wide.core.framework.storage.server;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

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
        if (history == null)
            return;

        reference.remove(valueHistory.getReference());
    }

    public void revert(final ObservableValue<?> observable)
    {
        final ObservableValueHistory valueHistory = history.get(observable);
        if (valueHistory == null || valueHistory.getHistory().empty())
            return;

        // Prevents recursive calls
        valueHistory.invalidate();

        final Object value = valueHistory.getHistory().pop();

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

        // If the history is empty remove the observable from the history
        if (valueHistory.getHistory().empty())
            remove(observable);
    }

    public void clear()
    {
        reference.clear();
        history.clear();
    }
}
