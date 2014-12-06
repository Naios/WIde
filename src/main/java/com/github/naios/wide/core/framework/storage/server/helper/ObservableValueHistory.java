package com.github.naios.wide.core.framework.storage.server.helper;

import java.util.Stack;

public class ObservableValueHistory
{
    private final ObservableValueStorageInfo reference;

    private final Stack<Object> history = new Stack<>();

    /**
     *  Prevents recursive calls from Rollbacks that inform changelisteners
     */
    private boolean nextIsValid = true;

    /**
     * Marks the storage for reinsertion
     * SQLBuilder needs to write an extra delete/insert query
     * to verify that our current database state is correct.
     */
    private boolean needsReinsert = false;

    public ObservableValueHistory(final ObservableValueStorageInfo reference)
    {
        this.reference = reference;
    }

    public ObservableValueStorageInfo getReference()
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


    public boolean reinsertNext()
    {
        final boolean cache = needsReinsert;
        needsReinsert = false;
        return cache;
    }
}