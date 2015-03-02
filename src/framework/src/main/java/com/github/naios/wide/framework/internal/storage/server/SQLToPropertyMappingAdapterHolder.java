
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
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.property.EnumProperty;
import com.github.naios.wide.api.property.FlagProperty;
import com.github.naios.wide.api.property.SimpleEnumProperty;
import com.github.naios.wide.api.property.SimpleFlagProperty;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.framework.internal.storage.mapping.MappingPlan;

public class SQLToPropertyMappingAdapterHolder
{
    private final static MappingAdapterHolder<ResultSet, ? extends ServerStorageStructure, ReadOnlyProperty<?>> INSTANCE = build();

    @SuppressWarnings("unchecked")
    public static <T extends ServerStorageStructure> MappingAdapterHolder<ResultSet, T, ReadOnlyProperty<?>> get()
    {
        return (MappingAdapterHolder<ResultSet, T, ReadOnlyProperty<?>>) INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    private static MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> build()
    {
        final MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // String
            .registerAdapter(new ServerMetaDataMappingAdapter<StringProperty, String>(StringProperty.class, String.class)
                {
                    @Override
                    protected String getDefault()
                    {
                        return "";
                    }

                    @Override
                    protected String getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getString(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public StringProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<String> value)
                    {
                        return new SimpleStringProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public String getPrimitiveValue(final StringProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected boolean setPrimitiveValue(final StringProperty me, final String value)
                    {
                        me.set(value);
                        return true;
                    }
                })
                 // FloatProperty
                .registerAdapter(new ServerMetaDataMappingAdapter<FloatProperty, Float>(FloatProperty.class, Float.class)
                {
                    @Override
                    protected Float getDefault()
                    {
                        return 0.f;
                    }

                    @Override
                    protected Float getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getFloat(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public FloatProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Float> value)
                    {
                        return new SimpleFloatProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public Float getPrimitiveValue(final FloatProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected boolean setPrimitiveValue(final FloatProperty me, final Float value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // DoubleProperty
            .registerAdapter(new ServerMetaDataMappingAdapter<DoubleProperty, Double>(DoubleProperty.class, Double.class)
                {
                    @Override
                    protected Double getDefault()
                    {
                        return 0.d;
                    }

                    @Override
                    protected Double getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getDouble(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public DoubleProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Double> value)
                    {
                        return new SimpleDoubleProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public Double getPrimitiveValue(final DoubleProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected boolean setPrimitiveValue(final DoubleProperty me, final Double value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // BooleanProperty
            .registerAdapter(new ServerMetaDataMappingAdapter<BooleanProperty, Boolean>(BooleanProperty.class, Boolean.class)
                {
                    @Override
                    protected Boolean getDefault()
                    {
                        return false;
                    }

                    @Override
                    protected Boolean getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getBoolean(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public BooleanProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Boolean> value)
                    {
                        return new SimpleBooleanProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public Boolean getPrimitiveValue(final BooleanProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected boolean setPrimitiveValue(final BooleanProperty me, final Boolean value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // IntegerProperty
            .registerAdapter(new ServerMetaDataMappingAdapter<IntegerProperty, Integer>(IntegerProperty.class, Integer.class)
                {
                    @Override
                    protected Integer getDefault()
                    {
                        return 0;
                    }

                    @Override
                    protected Integer getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getInt(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public IntegerProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Integer> value)
                    {
                        return new SimpleIntegerProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public Integer getPrimitiveValue(final IntegerProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected boolean setPrimitiveValue(final IntegerProperty me, final Integer value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // ReadOnlyIntegerProperty
            .registerAdapter(new ServerMetaDataMappingAdapter<ReadOnlyIntegerProperty, Integer>(ReadOnlyIntegerProperty.class, Integer.class)
                {
                    @Override
                    protected Integer getDefault()
                    {
                        return 0;
                    }

                    @Override
                    protected Integer getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getInt(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public ReadOnlyIntegerProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Integer> value)
                    {
                        return new ReadOnlyIntegerWrapper(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }

                    @Override
                    public Integer getPrimitiveValue(final ReadOnlyIntegerProperty me)
                    {
                        return me.get();
                    }
                })
             // Long Property
             .registerAdapter(new ServerMetaDataMappingAdapter<LongProperty, Long>(LongProperty.class, Long.class)
                {
                    @Override
                    protected Long getDefault()
                    {
                        return 0L;
                    }

                    @Override
                    public Long getPrimitiveValue(final LongProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected Long getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getLong(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public LongProperty create(final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Long> value)
                    {
                        return new SimpleLongProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }
                })
             // EnumProperty
             .registerAdapter(new ServerEnumMetaDataMappingAdapter<EnumProperty<? extends Enum<?>>, Enum<?>>(EnumProperty.class, Enum.class)
                {
                    @Override
                    protected Enum<?> getDefault()
                    {
                        return null;
                    }

                    private Enum<?> getDefaultForEnum(final MappingMetaData metaData)
                    {
                        return getEnum(metaData).getEnumConstants()[0];
                    }

                    @Override
                    protected Enum<?> getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        final Class<? extends Enum<?>> type = getEnum(metaData);

                        final int ordinal;
                        try
                        {
                            ordinal = from.getInt(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }

                        if (ordinal >= type.getEnumConstants().length)
                            throw new IllegalArgumentException(String.format("Ordinal %s at column is not part in enum %s!", ordinal, metaData.getName(), type.getName()));

                        return type.getEnumConstants()[ordinal];
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public EnumProperty<? extends Enum<?>> create(
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Enum<?>> value)
                    {
                        return new SimpleEnumProperty(getEnum(metaData), createBean(to, metaData), metaData.getName(), value.orElse(getDefaultForEnum(metaData)));
                    }

                    @Override
                    public Enum<?> getPrimitiveValue(final EnumProperty<? extends Enum<?>> me)
                    {
                        return me.get();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected boolean setPrimitiveValue(final EnumProperty<? extends Enum<?>> me, Enum<?> value)
                    {
                        final Optional<? extends Enum<?>> optionalValue = Optional.ofNullable(value);

                        if (optionalValue.isPresent())
                        {
                            if (value.getDeclaringClass().equals(getType().getRawType()))
                                return false;
                        }
                        else
                        {
                            value = me.getDefaultValue();
                        }

                        ((EnumProperty)me).set(value);

                        return true;
                    };
                })

                // FlagProperty
              .registerAdapter(new ServerEnumMetaDataMappingAdapter<FlagProperty<? extends Enum<?>>, Integer>(FlagProperty.class, Integer.class)
                {
                    @Override
                    protected Integer getDefault()
                    {
                        return 0;
                    }

                    @Override
                    protected Integer getMappedValue(final ResultSet from,
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        try
                        {
                            return from.getInt(metaData.getName());
                        }
                        catch (final SQLException e)
                        {
                            return getDefault();
                        }
                    }

                    @Override
                    public Integer getPrimitiveValue(final FlagProperty<? extends Enum<?>> me)
                    {
                        return me.get();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public FlagProperty<? extends Enum<?>> create(
                            final ServerStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Integer> value)
                    {
                        return new SimpleFlagProperty(getEnum(metaData), createBean(to, metaData), metaData.getName(), value.orElse(getDefault()));
                    }
                });

        return holder;
    }
}
