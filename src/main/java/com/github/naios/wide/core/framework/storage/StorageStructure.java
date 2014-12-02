package com.github.naios.wide.core.framework.storage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;
import com.github.naios.wide.core.framework.util.ClassUtil;
import com.github.naios.wide.core.framework.util.FormatterWrapper;

@SuppressWarnings("serial")
class MissingStorageNameException extends StorageException
{
    public MissingStorageNameException(final Class<? extends StorageStructure> type)
    {
        super(String.format("Structure %s defines no @StorageName!", type.getName()));
    }
}

public abstract class StorageStructure
{
    /**
     * Overwrite this if needed!
     * @return The GameBuilds that are valid for this structure.
     */
    public GameBuildMask getRequiredGameBuilds()
    {
        return GameBuildMask.ALL_BUILDS;
    }

    /**
     * Overwrite this!
     * @return The Annotation that marks specific fields of this structure.
     */
    protected abstract Class<? extends Annotation> getSpecificAnnotation();

    /**
     * Looks recursively for StorageName annotation
     *
     * @param The class that of the structure you want to search in.
     * @return The name of the storage
     *
     * @throws StorageException if the Structure doesn't contain a StorageName annotation
     */
    public static String GetStorageName(final Class<? extends StorageStructure> type) throws StorageException
    {
        return GetStorageNameRecursively(type, type);
    }

    private static String GetStorageNameRecursively(final Class<? extends StorageStructure> base,
            final Class<?> type) throws StorageException
    {
        if (type == null)
            throw new MissingStorageNameException(base);

        final StorageName name = type.getAnnotation(StorageName.class);
        if (name != null)
            return name.name();

        return GetStorageNameRecursively(base, type.getSuperclass());
    }

    public Field[] getAllFields()
    {
        return ClassUtil.getAnnotatedDeclaredFields(getClass(),
                getSpecificAnnotation(), true);
    }

    @Override
    public String toString()
    {
        final Field[] all_fields = getAllFields();

        final List<String> list = new LinkedList<String>();

        for (final Field field : all_fields)
        {
            if (!field.isAccessible())
                field.setAccessible(true);

            final StringBuilder builder = new StringBuilder()
                .append(field.getName())
                .append("=");

            try
            {
                Object object = field.get(this);
                if (object instanceof ObservableValue &&
                    !(object instanceof FlagProperty<?>))
                    object = ((ObservableValue<?>)object).getValue();

                builder.append(new FormatterWrapper(object).toString());
            }
            catch (final Exception e)
            {
                builder.append(Constants.STRING_EXCEPTION);
            }

            list.add(builder.toString());
        }

        return Arrays.toString(list.toArray());
    }
}
