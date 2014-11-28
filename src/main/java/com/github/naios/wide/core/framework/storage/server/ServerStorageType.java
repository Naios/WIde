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

public enum ServerStorageType
{
    // Integer
    INTEGER(IntegerProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new SimpleIntegerProperty(result.getInt(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new SimpleIntegerProperty();
                                             }
                                         }),
    READONLY_INTEGER(ReadOnlyIntegerProperty.class, true, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new ReadOnlyIntegerWrapper(result.getInt(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new ReadOnlyIntegerWrapper();
                                             }
                                         }),
    // Boolean
    BOOLEAN(BooleanProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new SimpleBooleanProperty(result.getBoolean(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new SimpleBooleanProperty();
                                             }
                                         }),
    READONLY_BOOLEAN(ReadOnlyBooleanProperty.class, true, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new ReadOnlyBooleanWrapper(result.getBoolean(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new ReadOnlyBooleanWrapper();
                                             }
                                         }),
    // Float
    FLOAT(FloatProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new SimpleFloatProperty(result.getFloat(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new SimpleFloatProperty();
                                             }
                                         }),
    READONLY_FLOAT(ReadOnlyFloatProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new ReadOnlyFloatWrapper(result.getFloat(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new ReadOnlyFloatWrapper();
                                             }
                                         }),
    // Double
    DOUBLE(DoubleProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new SimpleDoubleProperty(result.getDouble(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new SimpleDoubleProperty();
                                             }
                                         }),
    READONLY_DOUBLE(ReadOnlyDoubleProperty.class, false, (result, name) ->
                                         {
                                             try
                                             {
                                                 return new ReadOnlyDoubleWrapper(result.getDouble(name));
                                             }
                                             catch (final SQLException e)
                                             {
                                                 return new ReadOnlyDoubleWrapper();
                                             }
                                         }),
    // String
    STRING(StringProperty.class, false, (result, name) ->
                                        {
                                            try
                                            {
                                                return new SimpleStringProperty(result.getString(name));
                                            }
                                            catch (final SQLException e)
                                            {
                                                return new SimpleStringProperty();
                                            }
                                        }),
    READONLY_STRING(ReadOnlyStringProperty.class, true, (result, name) ->
                                        {
                                            try
                                            {
                                                return new ReadOnlyStringWrapper(result.getString(name));
                                            }
                                            catch (final SQLException e)
                                            {
                                                return new ReadOnlyStringWrapper();
                                            }
                                        });

    private final Class<? extends ObservableValue<?>> base;

    private final boolean isPossibleKey;

    private final BiFunction<ResultSet, String, ObservableValue<?>> create;

    private ServerStorageType(final Class<? extends ObservableValue<?>> base,
            final boolean isPossibleKey, final BiFunction<ResultSet, String, ObservableValue<?>> create)
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

    public ObservableValue<?> createFromResult(final ResultSet result, final String name)
    {
        return create.apply(result, name);
    }

    public static ServerStorageType SelectTypeOfField(final Field field)
    {
        final Class<?> fieldType = field.getType();
        for (final ServerStorageType me : values())
            if (me.base.equals(fieldType))
                return me;

        return null;
    }
}
