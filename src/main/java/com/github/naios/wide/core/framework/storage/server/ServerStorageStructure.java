package com.github.naios.wide.core.framework.storage.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.StorageStructure;
import com.github.naios.wide.core.framework.util.ClassUtil;

public abstract class ServerStorageStructure extends StorageStructure
{
    final private ServerStorage<?> owner;

    private ServerStorageStructureState state = ServerStorageStructureState.STATE_IN_SYNC;

    public ServerStorageStructure(final ServerStorage<?> owner)
    {
        this.owner = owner;
    }

    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ServerStorageEntry.class;
    }

    public static List<Field> GetPrimaryFields(final Class<? extends ServerStorageStructure> type)
    {
        final List<Field> list = new LinkedList<>();

        final Field[] fields = ClassUtil.getAnnotatedDeclaredFields(type,
                ServerStorageEntry.class, true);

        for (final Field field : fields)
            if (field.getAnnotation(ServerStorageEntry.class).key())
                list.add(field);

        return list;
    }

    public List<Object> getPrimaryKeys()
    {
        final List<Object> list = new LinkedList<>();

        for (final Field field : GetPrimaryFields(getClass()))
            {
                if (!field.isAccessible())
                    field.setAccessible(true);

                try
                {
                    list.add(field.get(this));
                }
                catch (final Exception e)
                {
                }
            }

        return list;
    }

    public static String GetNameOfField(final Field field)
    {
        final ServerStorageEntry annotation = field.getAnnotation(ServerStorageEntry.class);
        if (!annotation.name().equals(""))
            return annotation.name();
        else
            return field.getName();
    }

    public <T extends ServerStorageStructure> ServerStorageKey<T> getKey()
    {
        return new ServerStorageKey<T>(getPrimaryKeys().toArray());
    }

    protected ServerStorage<?> getOwner()
    {
        return owner;
    }

    protected void valueChanged(final Field field, final ObservableValue<?> me, final Object oldValue)
    {
        owner.valueChanged(this, field, me, oldValue);
    }

    public boolean hasState(final ServerStorageStructureState state)
    {
        return state.equals(this.state);
    }

    protected void setState(final ServerStorageStructureState state)
    {
        this.state = state;
    }

    public void delete()
    {
        setState(ServerStorageStructureState.STATE_DELETED);
        owner.structureDeleted(this);
    }

    @Override
    public int hashCode()
    {
        return getKey().hashCode();
    }
}
