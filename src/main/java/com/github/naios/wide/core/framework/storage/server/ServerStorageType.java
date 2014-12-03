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

import com.github.naios.wide.core.framework.storage.server.types.EnumProperty;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;

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
                    (result.getInt(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getInt(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getBoolean(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getBoolean(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getFloat(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getFloat(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getDouble(ServerStorageStructure.getNameOfField(field)));
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
                    (result.getDouble(ServerStorageStructure.getNameOfField(field)));
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
                   (result.getString(ServerStorageStructure.getNameOfField(field)));
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
                   (result.getString(ServerStorageStructure.getNameOfField(field)));
       }
       catch (final SQLException e)
       {
           return new ReadOnlyStringWrapper();
       }
    }),
    // Enum
    @SuppressWarnings({ "unchecked", "rawtypes" })
    ENUM(EnumProperty.class, false, (result, field) ->
    {
        final Class<?> type = getEnumClassHelper(field);

        try
        {
           return (new EnumProperty
                   (type, result.getInt(ServerStorageStructure.getNameOfField(field))));
        }
        catch (final SQLException e)
        {
           return (new EnumProperty(type));
        }
    }),
    // Flag
    @SuppressWarnings({ "unchecked", "rawtypes" })
    FLAG(FlagProperty.class, false, (result, field) ->
    {
        final Class<?> type = getEnumClassHelper(field);

        try
        {
           return (new FlagProperty
                   (type, result.getInt(ServerStorageStructure.getNameOfField(field))));
        }
        catch (final SQLException e)
        {
           return (new FlagProperty(type));
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

    public static ServerStorageType getType(final Field field)
    {
        return getType(field.getType());
    }

    public static ServerStorageType getType(final Class<?> type)
    {
        for (final ServerStorageType me : values())
            if (me.base.equals(type))
                return me;

        return null;
    }

    private static Class<?> getEnumClassHelper(final Field field)
    {
        final ServerStorageEntry annotation = field.getAnnotation(ServerStorageEntry.class);
        Class<?> type = null;

        if (!annotation.metaenum().isEmpty())
            try
            {
                type = Class.forName(annotation.metaenum());
            }
            catch (final Exception e)
            {
            }

        if (type == null || !type.isEnum())
            throw new NoMetaEnumException(field);

        return type;
    }

    // The Mapping process
    public static void doMapFieldToRecordFromResult(final Field field, final ServerStorageStructure record, final ResultSet result) throws ServerStorageException
    {
        if (!field.isAccessible())
            field.setAccessible(true);

        final ServerStorageType fieldType = ServerStorageType.getType(field);
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

    public static Object get(final ObservableValue<?> observable)
    {
        return observable.getValue();
    }

    public static boolean set(final ObservableValue<?> observable, final Object value)
    {
        // TODO move this into the type definition
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
        else
            return false;

        return true;
    }
}
