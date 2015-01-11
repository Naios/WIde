
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.util.ListIterator;
import java.util.Stack;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import com.github.naios.wide.framework.internal.storage.mapping.MappingImplementation;

class StructureChangeEventComposition implements StructureChangeEvent
{
    private Stack<StructureChangeEvent> subEvents =
            new Stack<>();

    public void add(final StructureChangeEvent event)
    {
        subEvents.push(event);
    }

    @Override
    public void revert() throws RollbackFailedException
    {
        while (!subEvents.isEmpty())
        {
            subEvents.pop().revert();
        }
    }

    @Override
    public void drop() throws RollbackFailedException
    {
        while (!subEvents.isEmpty())
        {
            subEvents.pop().drop();
        }
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

public class ServerStorageBaseImplementation
    implements ServerStoragePrivateBase, MappingImplementation<ServerStorageStructure>
{
    private ServerStorageStructure me;

    private final ListProperty<StructureChangeEvent> history =
            new SimpleListProperty<>();

    private final ObjectProperty<StructureState> structureState =
            new SimpleObjectProperty<>(StructureState.STATE_ALIVE);

    private final ObjectProperty<UpdatePolicy> updatePolicy =
            new SimpleObjectProperty<>(UpdatePolicy.DEFAULT_POLICY);

    private ServerStorageImpl<?> owner;

    @Override
    public void callback(final ServerStorageStructure structure)
    {
        this.me = structure;
    }

    @Override
    public ServerStorageImpl<?> getOwner()
    {
        return owner;
    }

    @Override
    public void setOwner(final ServerStorageImpl<?> owner)
    {
        this.owner = owner;
    }

    @Override
    public void delete()
    {
        addEvent(deleteEvent());
    }

    private void addEvent(final StructureChangeEvent event)
    {
        history.add(event);
    }

    private StructureDeletedEvent deleteEvent()
    {
        return new StructureDeletedEvent()
        {
            private StructureChangeEventComposition subEvents =
                    new StructureChangeEventComposition();

            {
                subEvents.add(resetEvent());
                structureState.set(StructureState.STATE_DELETED);
            }

            public void requiresLastCommitIsDeletedEvent() throws RollbackFailedException
            {
                if (history.get(history.size()) instanceof StructureDeletedEvent)
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
                createEvent();
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
                if (history.get(history.size()) instanceof StructureCreatedEvent)
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
                deleteEvent();
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
            private StructureChangeEventComposition subEvents =
                    new StructureChangeEventComposition();
            {
                subEvents.add(null);
            }

            public void requiresStructureIsAlive() throws RollbackFailedException
            {
                if (history.get(history.size()) instanceof StructureCreatedEvent)
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
                updateEvent(entry, oldValue);
            }

            @Override
            public ServerStorageStructure getStorageStructure()
            {
                return me;
            }
        };
    }

    @Override
    public void reset()
    {
        me.reset();
    }

    @Override
    public ReadOnlyObjectProperty<StructureState> state()
    {
        return structureState;
    }

    @Override
    public void release()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void rollback(final StructureChangeEvent event)
    {
        if (!history.contains(event))
            throw new IllegalArgumentException(String.format("%s is not contained in this structure (%s)!", event, me));

        final ListIterator<StructureChangeEvent> itr = history.listIterator(history.size());
        while (itr.hasPrevious())
        {
            final StructureChangeEvent cur = itr.previous();
            try
            {
                cur.drop();
            }
            catch (final RollbackFailedException e)
            {
                throw new IllegalStateException(e);
            }
            itr.remove();
        }
    }

    @Override
    public ReadOnlyListProperty<StructureChangeEvent> history()
    {
        return history;
    }

    @Override
    public void onCreate()
    {
        history.add(createEvent());
    }

    @Override
    public void onDelete()
    {
        history.add(deleteEvent());
    }

    @Override
    public void onUpdate(final Pair<ObservableValue<?>, MappingMetaData> entry, final Object oldValue)
    {
        history.add(updateEvent(entry, oldValue));
    }
}
