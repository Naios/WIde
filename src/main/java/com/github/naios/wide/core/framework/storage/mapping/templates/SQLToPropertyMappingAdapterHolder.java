
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.mapping.templates;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.mapping.MappingAdapter;
import com.github.naios.wide.core.framework.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.core.framework.storage.mapping.MappingMetaData;
import com.github.naios.wide.core.framework.storage.mapping.MappingPlan;
import com.github.naios.wide.core.framework.storage.server.AliasUtil;
import com.github.naios.wide.core.framework.storage.server.types.EnumProperty;
import com.github.naios.wide.core.framework.storage.server.types.FlagProperty;
import com.github.naios.wide.core.framework.util.FlagUtil;
import com.google.common.reflect.TypeToken;

public class SQLToPropertyMappingAdapterHolder
{
    public final static MappingAdapterHolder<ResultSet, ?, ObservableValue<?>> INSTANCE = build();

    private static MappingAdapterHolder<ResultSet, ?, ObservableValue<?>> build()
    {
        final MappingAdapterHolder<ResultSet, ?, ObservableValue<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // String
            .registerAdapter(TypeToken.of(StringProperty.class), new MappingAdapter<ResultSet, StringProperty>()
                {
                    @Override
                    public StringProperty map(final ResultSet from, final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return new SimpleStringProperty(from.getString(metaData.getName()));
                        } catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final StringProperty me, final Object value)
                    {
                        if (value instanceof String)
                        {
                            me.set((String)value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final StringProperty me)
                    {
                        me.set("");
                        return true;
                    }

                    @Override
                    public StringProperty create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new SimpleStringProperty(), value);
                    }
                })
             // FloatProperty
            .registerAdapter(TypeToken.of(FloatProperty.class), new MappingAdapter<ResultSet, FloatProperty>()
                {
                    @Override
                    public FloatProperty map(final ResultSet from, final MappingPlan plan,
                            final int index, final MappingMetaData metaData)
                    {
                        try
                        {
                            return new SimpleFloatProperty(from.getFloat(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final FloatProperty me, final Object value)
                    {
                        if (value instanceof Float)
                        {
                            me.set((float) value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final FloatProperty me)
                    {
                        me.set(0.f);
                        return true;
                    }

                    @Override
                    public FloatProperty create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new SimpleFloatProperty(), value);
                    }
                })
            // DoubleProperty
            .registerAdapter(TypeToken.of(DoubleProperty.class), new MappingAdapter<ResultSet, DoubleProperty>()
                {
                    @Override
                    public DoubleProperty map(final ResultSet from, final MappingPlan plan,
                            final int index, final MappingMetaData metaData)
                    {
                        try
                        {
                            return new SimpleDoubleProperty(from.getDouble(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final DoubleProperty me, final Object value)
                    {
                        if (value instanceof Double)
                        {
                            me.set((double) value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final DoubleProperty me)
                    {
                        me.set(0.d);
                        return true;
                    }

                    @Override
                    public DoubleProperty create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new SimpleDoubleProperty(), value);
                    }
                })
            // BooleanProperty
            .registerAdapter(TypeToken.of(BooleanProperty.class), new MappingAdapter<ResultSet, BooleanProperty>()
                {
                    @Override
                    public BooleanProperty map(final ResultSet from, final MappingPlan plan,
                            final int index, final MappingMetaData metaData)
                    {
                        try
                        {
                            return new SimpleBooleanProperty(from.getBoolean(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final BooleanProperty me, final Object value)
                    {
                        if (value instanceof Boolean)
                        {
                            me.set((boolean) value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final BooleanProperty me)
                    {
                        me.set(false);
                        return true;
                    }

                    @Override
                    public BooleanProperty create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new SimpleBooleanProperty(), value);
                    }
                })
            // IntegerProperty
            .registerAdapter(TypeToken.of(IntegerProperty.class), new MappingAdapter<ResultSet, IntegerProperty>()
                {
                    @Override
                    public IntegerProperty map(final ResultSet from, final MappingPlan plan,
                            final int index, final MappingMetaData metaData)
                    {
                        try
                        {
                            return new SimpleIntegerProperty(from.getInt(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final IntegerProperty me, final Object value)
                    {
                        if (value instanceof Integer)
                        {
                            me.set((int) value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final IntegerProperty me)
                    {
                        me.set(0);
                        return true;
                    }

                    @Override
                    public IntegerProperty create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new SimpleIntegerProperty(-1), value);
                    }
                })
            // ReadOnlyIntegerProperty
            .registerAdapter(TypeToken.of(ReadOnlyIntegerProperty.class), new MappingAdapter<ResultSet, ReadOnlyIntegerProperty>()
                {
                    @Override
                    public ReadOnlyIntegerProperty map(final ResultSet from, final MappingPlan plan,
                            final int index, final MappingMetaData metaData)
                    {
                        try
                        {
                            return new ReadOnlyIntegerWrapper(from.getInt(metaData.getName()));
                        } catch (final SQLException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public boolean isPossibleKey()
                    {
                        return true;
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyIntegerProperty me)
                    {
                        return me.getValue();
                    }

                    @Override
                    public ReadOnlyIntegerProperty create(final MappingPlan plan,
                            final int index, final MappingMetaData metaData, final Object value)
                    {
                        if (value instanceof Integer)
                            return new ReadOnlyIntegerWrapper((int)value);
                        else
                            return null;
                    }
                })
            // Enum Property
            .registerAdapter(TypeToken.of(EnumProperty.class), new MappingAdapter<ResultSet, EnumProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public EnumProperty map(final ResultSet from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return new EnumProperty(AliasUtil.getEnum(metaData.getAlias()), from.getInt(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final EnumProperty<?> me, final Object value)
                    {
                        if (value instanceof Integer)
                        {
                            me.set((int)value);
                            return true;
                        }

                        final Class<? extends Enum<?>> enumeration = me.getEnum();
                        if (!enumeration.isAssignableFrom(value.getClass()))
                            return false;

                        for (final Enum<?> info : enumeration.getEnumConstants())
                            if (info.equals(value))
                            {
                                me.set(info.ordinal());
                                return true;
                            }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final EnumProperty<?> me)
                    {
                        me.set(0);
                        return true;
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public EnumProperty<?> create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new EnumProperty(AliasUtil.getEnum(metaData.getAlias())), value);
                    }
                })
            // Flag Property
            .registerAdapter(TypeToken.of(FlagProperty.class), new MappingAdapter<ResultSet, FlagProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public FlagProperty map(final ResultSet from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return new FlagProperty(AliasUtil.getEnum(metaData.getAlias()), from.getInt(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            return null;
                        }
                    }

                    @Override
                    public boolean set(final FlagProperty<?> me, final Object value)
                    {
                        if (value instanceof Integer)
                        {
                            me.set((int)value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean setDefault(final FlagProperty<?> me)
                    {
                        me.set(FlagUtil.DEFAULT_VALUE);
                        return true;
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public FlagProperty<?> create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new FlagProperty(AliasUtil.getEnum(metaData.getAlias())), value);
                    }
                });

        return holder;
    }
}
