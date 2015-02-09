
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerMappingBean;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.FlagUtil;
import com.github.naios.wide.entities.util.EnumProperty;
import com.github.naios.wide.entities.util.FlagProperty;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapter;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.framework.internal.storage.mapping.MappingPlan;
import com.google.common.reflect.TypeToken;

public class SQLToPropertyMappingAdapterHolder
{
    private abstract class SQLMappingAdapter<T extends ReadOnlyProperty<?>>
        extends MappingAdapter<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>,T>
    {
        public SQLMappingAdapter(final Class<T> type)
        {
            super(type);
        }

        protected ServerMappingBean createBean(final ServerStorageStructure to,
                final MappingMetaData metaData)
        {
            return new ServerMappingBean()
            {
                @Override
                public ServerStorageStructure getStructure()
                {
                    return to;
                }

                @Override
                public MappingMetaData getMappingMetaData()
                {
                    return metaData;
                }
            };
        }
    }

    public final static MappingAdapterHolder<ResultSet, ?, ReadOnlyProperty<?>> INSTANCE = build();

    private static MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> build()
    {
        final MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // String
            .registerAdapter(new SQLMappingAdapter<StringProperty>(StringProperty.class)
                {
                    @Override
                    protected Object getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getString(metaData.getName());
                    }

                    @Override
                    public StringProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Object> value)
                    {
                        return setValueOrDefaultIfNotPresent(new SimpleStringProperty(createBean(to, metaData), metaData.getName()), value);
                    }

                    @Override
                    protected boolean setAdaptedType(final StringProperty me, final Object value)
                    {
                        if (value instanceof String)
                        {
                            me.set((String)value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    protected Object getDefault()
                    {
                        return "";
                    }
                })
             // FloatProperty
            .registerAdapter(new SQLMappingAdapter<FloatProperty>(FloatProperty.class)
                {
                    @Override
                    public FloatProperty map(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan,
                            final int index,
                            final MappingMetaData metaData)
                    {
                        final String name = metaData.getName();
                        final float value = from.getFloat(name);

                        return new SimpleFloatProperty(createBean(to, metaData), name, value);
                    }

                    @Override
                    public FloatProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return setValueOrDefaultIfNotPresent(new SimpleFloatProperty(createBean(to, metaData), metaData.getName()), value);
                    }

                    @Override
                    protected boolean setOverwritten(final FloatProperty me, final Object value)
                    {
                        if (value instanceof Float)
                        {
                            me.set((float)value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    protected Object getDefault()
                    {
                        return "";
                    }
                })
            // DoubleProperty
            .registerAdapter(new SQLMappingAdapter<DoubleProperty>(DoubleProperty.class)
                {
                    @Override
                    protected Object getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public DoubleProperty map(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan,
                            final int index,
                            final MappingMetaData metaData)
                    {
                        final String name = ;
                        final float value = from.getFloat(name);

                        return new SimpleDoubleProperty(createBean(to, metaData), name, value);
                    }

                    @Override
                    public DoubleProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return setValueOrDefaultIfNotPresent(new SimpleDoubleProperty(createBean(to, metaData), metaData.getName()), value);
                    }

                    @Override
                    protected boolean setOverwritten(final DoubleProperty me, final Object value)
                    {
                        if (value instanceof Float)
                        {
                            me.set((float)value);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    protected Object getDefault()
                    {
                        return "";
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
                    protected boolean setOverwritten(final ADAPTED_TYPE me, final Object value)
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
                        return setValueOrDefaultIfNotPresent(new SimpleBooleanProperty(), value);
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
                    protected boolean setOverwritten(final ADAPTED_TYPE me, final Object value)
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
                        return setValueOrDefaultIfNotPresent(new SimpleIntegerProperty(-1), value);
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
                            return new EnumProperty(FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias()), from.getInt(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            return null;
                        }
                    }

                    @Override
                    protected boolean setOverwritten(final ADAPTED_TYPE me, final Object value)
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
                        return setValueOrDefaultIfNotPresent(new EnumProperty(FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias())), value);
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
                            return new FlagProperty(FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias()), from.getInt(metaData.getName()));
                        }
                        catch (final SQLException e)
                        {
                            return null;
                        }
                    }

                    @Override
                    protected boolean setOverwritten(final ADAPTED_TYPE me, final Object value)
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
                        return setValueOrDefaultIfNotPresent(new FlagProperty(FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias())), value);
                    }
                });

        return holder;
    }
}
