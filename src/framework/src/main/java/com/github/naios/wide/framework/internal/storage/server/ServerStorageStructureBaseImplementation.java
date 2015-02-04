
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.util.ListIterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.RollbackFailedException;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureChangeEvent;
import com.github.naios.wide.api.framework.storage.server.StructureCreatedEvent;
import com.github.naios.wide.api.framework.storage.server.StructureDeletedEvent;
import com.github.naios.wide.api.framework.storage.server.StructureModifyEvent;
import com.github.naios.wide.api.framework.storage.server.StructureResetEvent;
import com.github.naios.wide.api.framework.storage.server.StructureState;
import com.github.naios.wide.api.framework.storage.server.UpdatePolicy;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.framework.internal.storage.mapping.MappingCallback;

interface Rollbackable
{
    public boolean rollback(StructureChangeEvent event) throws RollbackFailedException;
}

class StructureChangeEventCompositum
    extends SimpleListProperty<StructureChangeEvent>
    implements StructureChangeEvent
{
    @Override
    public void revert() throws RollbackFailedException
    {
        backwardRollback(new Rollbackable()
        {
            @Override
            public boolean rollback(final StructureChangeEvent event)
                    throws RollbackFailedException
            {
                event.revert();
                return true;
            }
        });
    }

    @Override
    public void drop() throws RollbackFailedException
    {
        backwardRollback(new Rollbackable()
        {
            @Override
            public boolean rollback(final StructureChangeEvent event)
                    throws RollbackFailedException
            {
                event.drop();
                return true;
            }
        });
    }

    public void backwardRollback(final Rollbackable rollbackable)
    {
        final ListIterator<StructureChangeEvent> itr = listIterator(size());
        boolean _continue = true;
        while (_continue && itr.hasPrevious())
        {
            final StructureChangeEvent current = itr.previous();
            try
            {
                _continue = rollbackable.rollback(current);
            }
            catch (final RollbackFailedException e)
            {
                throw new IllegalStateException(e);
            }
        }
        clear();
    }
}

abstract class AbstractStructureModifyEvent
    implements StructureModifyEvent
{
    protected final Pair<ObservableValue<?>, MappingMetaData> entry;

    protected final Object oldValue;

    public AbstractStructureModifyEvent(final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
    {
        this.entry = entry;

        this.oldValue = oldValue;
    }

    public Pair<ObservableValue<?>, MappingMetaData> getEntry()
    {
        return entry;
    }

    @Override
    public ObservableValue<?> getObservable()
    {
        return entry.first();
    }

    @Override
    public Object getOldValue()
    {
        return oldValue;
    }
}

class HistoryRedirect
{
    private final StructureChangeEventCompositum history =
            new StructureChangeEventCompositum();

    private final StructureChangeEventCompositum DEFAULT_HISTORY = history;

    private final ObjectProperty<StructureChangeEventCompositum> currentEventStack =
            new SimpleObjectProperty<StructureChangeEventCompositum>(DEFAULT_HISTORY);

    protected ReadOnlyListProperty<StructureChangeEvent> defaultHistory()
    {
        return history;
    }

    protected void pushEvent(final StructureChangeEvent event)
    {
        currentEventStack.get().add(event);
    }

    protected void setEventHistory(final StructureChangeEventCompositum history)
    {
        if (currentEventStack.get() != DEFAULT_HISTORY)
            throw new IllegalStateException("Current history is not the default history!");

        currentEventStack.set(history);
    }

    protected void releaseEventHistory(final StructureChangeEventCompositum history)
    {
        if (currentEventStack.get() != history)
            throw new IllegalStateException("History you want to release is not the current history!");

        currentEventStack.set(DEFAULT_HISTORY);
    }

    public StructureChangeEvent getLastEvent()
    {
        return history.get(history.size());
    }

    protected void rollback(final StructureChangeEvent eventToRollback)
    {
        history.backwardRollback(new Rollbackable()
        {
            @Override
            public boolean rollback(final StructureChangeEvent event)
                    throws RollbackFailedException
            {
                if (event.equals(eventToRollback))
                    return false;

                event.drop();
                return true;
            }
        });
    }
}

public class ServerStorageStructureBaseImplementation
    implements ServerStorageStructurePrivateBase, MappingCallback<ServerStorageStructure>
{
    private ServerStorageStructure me;

    private ChangeTrackerImpl changeTracker;

    private final HistoryRedirect history = new HistoryRedirect();

    private final ObjectProperty<StructureChangeEvent> head =
            new SimpleObjectProperty<StructureChangeEvent>();

    private final ObjectProperty<StructureState> structureState =
            new SimpleObjectProperty<>(StructureState.STATE_ALIVE);

    private final ObjectProperty<UpdatePolicy> updatePolicy =
            new SimpleObjectProperty<>(UpdatePolicy.DEFAULT_POLICY);

    private ServerStorageImpl<?> owner;

    @Override
    public void callback(final ServerStorageStructure structure)
    {
        this.me = structure;

        for (final Pair<ObservableValue<?>, MappingMetaData> entry : me)
            entry.first().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(final ObservableValue<? extends Object> observable,
                    final Object oldValue, final Object newValue)
            {
                onUpdate(entry, oldValue);
            }
        });
    }

    @Override
    public ServerStorageImpl<?> getOwner()
    {
        return owner;
    }

    @Override
    public void setOwnerAndTracker(final ServerStorageImpl<?> owner, final ChangeTrackerImpl changeTracker)
    {
        this.owner = owner;
        this.changeTracker = changeTracker;
    }

    /**
     * Empty method to show that the event is dropped.
     */
    private static void dropEvent(final StructureChangeEvent event)
    {
    }

    @Override
    public synchronized void delete()
    {
        onDelete();
    }

    private StructureDeletedEvent deleteEvent()
    {
        return new StructureDeletedEvent()
        {
            private StructureChangeEventCompositum subEvents =
                    new StructureChangeEventCompositum();

            {
                subEvents.add(resetEvent());
                structureState.set(StructureState.STATE_DELETED);
            }

            public void requiresLastCommitIsDeletedEvent() throws RollbackFailedException
            {
                if (history.getLastEvent() instanceof StructureDeletedEvent)
                    throw new RollbackFailedException("Last ChangeEvent isn't instanceof StructureDeletedEvent!");

                if (!structureState.get().equals(StructureState.STATE_DELETED))
                    throw new IllegalStateException("Last event is StructureDeletedEvent but state is not deleted!");
            }

            @Override
            public void revert() throws RollbackFailedException
            {
                requiresLastCommitIsDeletedEvent();
                onCreate();
            }

            @Override
            public void drop() throws RollbackFailedException
            {
                requiresLastCommitIsDeletedEvent();
                dropEvent(createEvent());
                subEvents.drop();
            }

            @Override
            public ServerStorageStructure getStorageStructure()
            {
                return me;
            }
        };
    }

    private StructureCreatedEvent createEvent()
    {
        return new StructureCreatedEvent()
        {
            {
                structureState.set(StructureState.STATE_ALIVE);
            }

            public void requiresLastCommitIsCreatedEvent() throws RollbackFailedException
            {
                if (history.getLastEvent() instanceof StructureCreatedEvent)
                    throw new RollbackFailedException("Last ChangeEvent isn't instanceof requiresLastCommitIsCreatedEvent!");

                if (!structureState.get().equals(StructureState.STATE_ALIVE))
                    throw new IllegalStateException("Last event is StructureCreatedEvent but state is not alive!");
            }

            @Override
            public void revert() throws RollbackFailedException
            {
                requiresLastCommitIsCreatedEvent();
                onDelete();
            }

            @Override
            public void drop() throws RollbackFailedException
            {
                requiresLastCommitIsCreatedEvent();
                dropEvent(deleteEvent());
            }

            @Override
            public ServerStorageStructure getStorageStructure()
            {
                return me;
            }
        };
    }

    private StructureResetEvent resetEvent()
    {
        return new StructureResetEvent()
        {
            private StructureChangeEventCompositum subEvents =
                    new StructureChangeEventCompositum();

            {
                history.setEventHistory(subEvents);

                for (final Pair<ObservableValue<?>, MappingMetaData> entry : me)
                    owner.resetValueOfObservable(entry);

                history.releaseEventHistory(subEvents);
            }

            public void requiresStructureIsAlive() throws RollbackFailedException
            {
                if (history.getLastEvent() instanceof StructureCreatedEvent)
                    throw new RollbackFailedException("Last ChangeEvent isn't instanceof requiresLastCommitIsCreatedEvent!");

                if (!structureState.get().equals(StructureState.STATE_ALIVE))
                    throw new IllegalStateException("Last event is StructureDeletedEvent but state is different!");
            }

            @Override
            public void revert() throws RollbackFailedException
            {
                requiresStructureIsAlive();
                subEvents.revert();
            }

            @Override
            public void drop() throws RollbackFailedException
            {
                requiresStructureIsAlive();
                subEvents.drop();
            }

            @Override
            public ServerStorageStructure getStorageStructure()
            {
                return me;
            }
        };
    }

    private StructureModifyEvent updateEvent(final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
    {
        return new AbstractStructureModifyEvent(entry, oldValue)
        {
            @Override
            public void revert() throws RollbackFailedException
            {
                onUpdate(entry, oldValue);
            }

            @Override
            public void drop() throws RollbackFailedException
            {
                dropEvent(updateEvent(entry, oldValue));
            }

            @Override
            public ServerStorageStructure getStorageStructure()
            {
                return me;
            }
        };
    }

    @Override
    public ReadOnlyObjectProperty<StructureState> state()
    {
        return structureState;
    }

    @Override
    public ObjectProperty<UpdatePolicy> updatePolicy()
    {
        return updatePolicy;
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public synchronized void rollback(final StructureChangeEvent eventToRollback)
    {
        if (!history.defaultHistory().contains(eventToRollback))
            throw new IllegalArgumentException(String.format("%s is not contained in this structure (%s)!", eventToRollback, me));

        history.rollback(eventToRollback);
    }

    @Override
    public ReadOnlyListProperty<StructureChangeEvent> history()
    {
        return history.defaultHistory();
    }

    @Override
    public synchronized void reset()
    {
        history.pushEvent(resetEvent());
    }

    @Override
    public synchronized void onCreate()
    {
        history.pushEvent(createEvent());
    }

    @Override
    public synchronized void onDelete()
    {
        history.pushEvent(deleteEvent());
    }

    @Override
    public synchronized void onUpdate(final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
    {
        history.pushEvent(updateEvent(entry, oldValue));
    }
}
