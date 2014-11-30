package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;
import com.github.naios.wide.core.framework.storage.server.types.ReadOnlyFlagProperty;
import com.github.naios.wide.core.framework.storage.server.types.ReadOnlyFlagWrapper;
import com.github.naios.wide.core.framework.storage.server.types.SimpleFlagProperty;

@SuppressWarnings("serial")
class NoMetaEnumException extends ServerStorageException
{
    public NoMetaEnumException(final Field field)
    {
        super(String.format("Field %s defines no valid metaenum!", field.getName()));
    }
}

public enum ServerStorageType
{
    // Integer
    INTEGER(IntegerProperty.class, false, (result, field) ->
    {
        try
        {
            return new SimpleIntegerProperty
                    (result.getInt(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new SimpleIntegerProperty();
        }
    }),
    READONLY_INTEGER(ReadOnlyIntegerProperty.class, true, (result, field) ->
    {
        try
        {
            return new ReadOnlyIntegerWrapper
                    (result.getInt(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new ReadOnlyIntegerWrapper();
        }
    }),
    // Boolean
    BOOLEAN(BooleanProperty.class, false, (result, field) ->
    {
        try
        {
            return new SimpleBooleanProperty
                    (result.getBoolean(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new SimpleBooleanProperty();
        }
    }),
    READONLY_BOOLEAN(ReadOnlyBooleanProperty.class, true, (result, field) ->
    {
        try
        {
            return new ReadOnlyBooleanWrapper
                    (result.getBoolean(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new ReadOnlyBooleanWrapper();
        }
    }),
    // Float
    FLOAT(FloatProperty.class, false, (result, field) ->
    {
        try
        {
            return new SimpleFloatProperty
                    (result.getFloat(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new SimpleFloatProperty();
        }
    }),
    READONLY_FLOAT(ReadOnlyFloatProperty.class, false, (result, field) ->
    {
        try
        {
            return new ReadOnlyFloatWrapper
                    (result.getFloat(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new ReadOnlyFloatWrapper();
        }
    }),
    // Double
    DOUBLE(DoubleProperty.class, false, (result, field) ->
    {
        try
        {
            return new SimpleDoubleProperty
                    (result.getDouble(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new SimpleDoubleProperty();
        }
    }),
    READONLY_DOUBLE(ReadOnlyDoubleProperty.class, false, (result, field) ->
    {
        try
        {
            return new ReadOnlyDoubleWrapper
                    (result.getDouble(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new ReadOnlyDoubleWrapper();
        }
    }),
    // String
    STRING(StringProperty.class, false, (result, field) ->
    {
        try
        {
            return new SimpleStringProperty
                   (result.getString(ServerStorageStructure.GetNameOfField(field)));
        }
        catch (final SQLException e)
        {
            return new SimpleStringProperty();
        }
    }),
    READONLY_STRING(ReadOnlyStringProperty.class, true, (result, field) ->
    {
       try
       {
           return new ReadOnlyStringWrapper
                   (result.getString(ServerStorageStructure.GetNameOfField(field)));
       }
       catch (final SQLException e)
       {
           return new ReadOnlyStringWrapper();
       }
    }),
    // Flag
    @SuppressWarnings("unchecked")
    FLAG(FlagProperty.class, false, (result, field) ->
    {
        final Class<?> type = GetEnumClassHelper(field);

        try
        {
           return (IntegerProperty)(new SimpleFlagProperty
                   (GetEnumClassHelper(field), result.getInt(ServerStorageStructure.GetNameOfField(field))));
        }
        catch (final SQLException e)
        {
           return (IntegerProperty)(new SimpleFlagProperty(GetEnumClassHelper(field)));
        }
    }),
    @SuppressWarnings("unchecked")
    READONLY_FLAG(ReadOnlyFlagProperty.class, true, (result, field) ->
    {
        try
        {
            return (ReadOnlyIntegerProperty)(new ReadOnlyFlagWrapper
                    (GetEnumClassHelper(field), result.getInt(ServerStorageStructure.GetNameOfField(field))));
        }
        catch (final SQLException e)
        {
            return (ReadOnlyIntegerProperty)(new ReadOnlyFlagWrapper(GetEnumClassHelper(field)));
        }
    });

    private final Class<? extends ObservableValue<?>> base;

    private final boolean isPossibleKey;

    private final BiFunction<ResultSet, Field, ObservableValue<?>> create;

    private ServerStorageType(final Class<? extends ObservableValue<?>> base,
            final boolean isPossibleKey, final BiFunction<ResultSet, Field, ObservableValue<?>> create)
    {
        this.base = base;
        this.isPossibleKey = isPossibleKey;
        this.create = create;
    }

    public Class<? extends ObservableValue<?>> getBase()
    {
        return base;
    }

    public boolean getIsPossibleKey()
    {
        return isPossibleKey;
    }

    public ObservableValue<?> createFromResult(final ResultSet result, final Field field)
    {
        return create.apply(result, field);
    }

    public static ServerStorageType SelectTypeOfField(final Field field)
    {
        final Class<?> fieldType = field.getType();
        for (final ServerStorageType me : values())
            if (me.base.equals(fieldType))
                return me;

        return null;
    }

    private static Class<?> GetEnumClassHelper(final Field field)
    {
        final ServerStorageEntry annotation = field.getAnnotation(ServerStorageEntry.class);
        Class<?> type = null;

        if (annotation.metaenum().isEmpty())
            type = Class.forName(annotation.metaenum());

        if (type == null || !type.isEnum())
            throw new NoMetaEnumException(field);

        return type;
    }

    // The Mapping process
    public static void MapFieldToRecordFromResult(final Field field, final ServerStorageStructure record, final ResultSet result) throws ServerStorageException
    {
        if (!field.isAccessible())
            field.setAccessible(true);

        final ServerStorageType fieldType = ServerStorageType.SelectTypeOfField(field);
        final ObservableValue<?> value = fieldType.createFromResult(result, field);

        value.addListener(new ServerStoragedEntryChangeListener(record, field));

        try
        {
            field.set(record, value);
        }
        catch (final Exception e)
        {
            // TODO
            // Throw Mapping exception
        }
    }
}
