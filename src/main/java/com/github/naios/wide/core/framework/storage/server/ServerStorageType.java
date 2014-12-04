package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

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
    // TODO Find another unhacky way fix this generetic shit problem...

    // Integer
    INTEGER((type) ->
            {
                return IntegerProperty.class.equals(type) ||
                       SimpleIntegerProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((IntegerProperty) me).set((int) value);
            }),
    READONLY_INTEGER((type) ->
            {
                return ReadOnlyIntegerProperty.class.equals(type) ||
                       ReadOnlyIntegerWrapper.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                assert (false);
            }),
    // Boolean
    BOOLEAN((type) ->
            {
                return BooleanProperty.class.equals(type) ||
                       SimpleBooleanProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((BooleanProperty) me).set((boolean) value);
            }),
    READONLY_BOOLEAN((type) ->
            {
                return ReadOnlyBooleanProperty.class.equals(type) ||
                       ReadOnlyBooleanWrapper.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                assert (false);
            }),
    // Float
    FLOAT((type) ->
            {
                return FloatProperty.class.equals(type) ||
                       SimpleFloatProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((FloatProperty) me).set((float) value);
            }),
    READONLY_FLOAT((type) ->
            {
                return ReadOnlyFloatProperty.class.equals(type) ||
                       ReadOnlyFloatWrapper.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                assert (false);
            }),
    // Double
    DOUBLE((type) ->
            {
                return DoubleProperty.class.equals(type) ||
                       SimpleDoubleProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((DoubleProperty) me).set((double) value);
            }),
    READONLY_DOUBLE((type) ->
            {
                return ReadOnlyDoubleProperty.class.equals(type) ||
                       ReadOnlyDoubleWrapper.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                assert (false);
            }),
    // String
    STRING((type) ->
            {
                return StringProperty.class.equals(type) ||
                       SimpleStringProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((StringProperty) me).set((String) value);
            }),
    READONLY_STRING((type) ->
            {
                return ReadOnlyStringProperty.class.equals(type) ||
                       ReadOnlyStringWrapper.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                assert (false);
            }),
    // Enum
    @SuppressWarnings({ "unchecked", "rawtypes" })
    ENUM((type) ->
            {
                return EnumProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((IntegerProperty) me).set((int) value);
            }),
    // Flag
    @SuppressWarnings({ "unchecked", "rawtypes" })
    FLAG((type) ->
            {
                return FlagProperty.class.equals(type);
            },
            (result, field) ->
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
            },
            (me, value) ->
            {
                ((IntegerProperty) me).set((int) value);
            });

    private final Predicate<Class<?>> instanceOfCheck;

    private final BiFunction<ResultSet, Field, ObservableValue<?>> create;

    private final BiConsumer<ObservableValue<?>, Object> set;

    private ServerStorageType(final Predicate<Class<?>> instanceOfCheck,
                                final BiFunction<ResultSet, Field, ObservableValue<?>> create,
                                    final BiConsumer<ObservableValue<?>, Object> set)
    {
        this.instanceOfCheck = instanceOfCheck;
        this.create = create;
        this.set = set;
    }

    public boolean isPossibleKey()
    {
        return equals(READONLY_INTEGER) || equals(READONLY_STRING);
    }

    public ObservableValue<?> createFromResult(final ResultSet result, final Field field)
    {
        return create.apply(result, field);
    }

    public static ServerStorageType getType(final Field field)
    {
        return getType(field.getType());
    }

    public static ServerStorageType getType(final Class<?> object)
    {
        for (final ServerStorageType me : values())
            if (me.instanceOfCheck.test(object))
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

        value.addListener(new ServerStoragedChangeListener(record, field));

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
        final ServerStorageType storageType = getType(observable.getClass());
        if (storageType == null)
            return false;

        storageType.set.accept(observable, value);
        return true;
    }
}
