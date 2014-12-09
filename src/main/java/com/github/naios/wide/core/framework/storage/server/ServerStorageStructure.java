package com.github.naios.wide.core.framework.storage.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.StorageStructure;
import com.github.naios.wide.core.framework.storage.server.helper.StructureState;
import com.github.naios.wide.core.framework.util.Pair;
import com.github.naios.wide.core.session.database.DatabaseType;

class IllegalStorageName extends ServerStorageException
{
    public IllegalStorageName()
    {
        super("Storage name is not valid. [id:table or table]!");
    }
}

public abstract class ServerStorageStructure extends StorageStructure implements Iterable<Pair<ObservableValue<?>, Field>>
{
    final private ServerStorage<?> owner;

    private final ObjectProperty<StructureState> state = new SimpleObjectProperty<>(StructureState.STATE_IN_SYNC);

    private ServerStorageKey<?> key = null;

    public ServerStorageStructure(final ServerStorage<?> owner)
    {
        this.owner = owner;
    }

    public ObservableValue<?> getObservableValue(final Field field)
    {
        if (!field.isAccessible())
            field.setAccessible(true);

        try
        {
            return (ObservableValue<?>) field.get(this);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ServerStorageEntry.class;
    }

    public List<Field> getPrimaryFields()
    {
        return getPrimaryFields(getClass());
    }

    public static List<Field> getPrimaryFields(final Class<? extends ServerStorageStructure> type)
    {
        final List<Field> list = new LinkedList<>();

        final Field[] fields = StorageStructure.getAllFields(type, ServerStorageEntry.class);

        for (final Field field : fields)
            if (field.getAnnotation(ServerStorageEntry.class).key())
                list.add(field);

        return list;
    }

    @SuppressWarnings("rawtypes")
    public ServerStorageKey<?> getPrimaryKey()
    {
        if (key == null)
        {
            final List<Object> keyCache = new ArrayList<>();

            for (final Field field : getPrimaryFields())
                {
                    if (!field.isAccessible())
                        field.setAccessible(true);

                    try
                    {
                        keyCache.add(field.get(this));
                    }
                    catch (final Exception e)
                    {
                    }
                }

            key = new ServerStorageKey(keyCache.toArray());
        }

        return key;
    }

    public static String getNameOfField(final Field field)
    {
        final ServerStorageEntry annotation = field.getAnnotation(ServerStorageEntry.class);
        if (!annotation.name().equals(""))
            return annotation.name();
        else
            return field.getName();
    }

    public static String getTableNameFromStructure(final Class<? extends StorageStructure> type) throws ServerStorageException
    {
        final String[] name = StorageStructure.getStorageName(type).split(":");
        if (name.length < 1)
            throw new IllegalStorageName();

        return name[name.length - 1];
    }

    public static DatabaseType getTableTypeFromStructure(final Class<? extends StorageStructure> type) throws ServerStorageException
    {
        final String[] name = StorageStructure.getStorageName(type).split(":");
        if (name.length < 2)
            throw new IllegalStorageName();

        return DatabaseType.getFromId(name[0]);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerStorageStructure> ServerStorageKey<T> getKey()
    {
        return (ServerStorageKey<T>) getPrimaryKey();
    }

    public ServerStorage<?> getOwner()
    {
        return owner;
    }

    protected void valueChanged(final Field field, final ObservableValue<?> me, final Object oldValue)
    {
        owner.onValueChanged(this, field, me, oldValue);
    }

    public boolean hasState(final StructureState state)
    {
        return this.state.get().equals(state);
    }

    public ReadOnlyObjectProperty<StructureState> state()
    {
        return state;
    }

    public ObjectProperty<StructureState> writeableState()
    {
        return state;
    }

    /**
     * Deletes the structure
     */
    public void delete()
    {
        owner.onStructureDeleted(this);
    }

    /**
     * Loads default value to non key fields
     */
    public void reset()
    {
        for (final Pair<ObservableValue<?>, Field> value : this)
            ServerStorageFieldType.setDefault(value.first());
    }

    private class ServerStorageIterator implements Iterator<Pair<ObservableValue<?>, Field>>
    {
        private final ServerStorageStructure storage;

        private final Field[] fields;

        private int i = 0;

        protected ServerStorageIterator(final ServerStorageStructure storage)
        {
            this.storage = storage;
            fields = storage.getAllFieldsFromThis();
        }

        @Override
        public boolean hasNext()
        {
            return i < fields.length;
        }

        @Override
        public Pair<ObservableValue<?>, Field> next()
        {
            try
            {
                final Pair<ObservableValue<?>, Field> pair = new Pair<>(storage.getObservableValue(fields[i]), fields[i++]);
                return pair;
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public Iterator<Pair<ObservableValue<?>, Field>> iterator()
    {
        return new ServerStorageIterator(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ServerStorageStructure other = (ServerStorageStructure) obj;
        if (owner == null)
        {
            if (other.owner != null)
                return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return getKey().hashCode();
    }
}
