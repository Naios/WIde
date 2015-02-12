
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
import com.github.naios.wide.api.property.EnumProperty;
import com.github.naios.wide.api.property.FlagProperty;
import com.github.naios.wide.api.property.SimpleEnumProperty;
import com.github.naios.wide.api.property.SimpleFlagProperty;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapter;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.framework.internal.storage.mapping.MappingPlan;

public class SQLToPropertyMappingAdapterHolder
{
    public final static MappingAdapterHolder<ResultSet, ?, ReadOnlyProperty<?>> INSTANCE = build();

    @SuppressWarnings("rawtypes")
    private static MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> build()
    {
        abstract class SQLMappingAdapter<T extends ReadOnlyProperty<?>, P>
                extends MappingAdapter<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>, T, P>
        {
            public SQLMappingAdapter(final Class<T> type, final Class<P> primitive)
            {
                super(type, primitive);
            }

            class ServerMappingBeanImpl
                implements ServerMappingBean
            {
                private final ServerStorageStructure to;

                private final MappingMetaData metaData;

                public ServerMappingBeanImpl(final ServerStorageStructure to, final MappingMetaData metaData)
                {
                    this.to = to;
                    this.metaData = metaData;
                }

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

                @Override
                public String toString()
                {
                    return String.format("ServerMappingBean(%s)", to.getOwner().getTableName());
                };
            }

            protected ServerMappingBean createBean(final ServerStorageStructure to, final MappingMetaData metaData)
            {
                return new ServerMappingBeanImpl(to, metaData);
            }
        }

        abstract class EnumSQLMappingAdapter<T extends ReadOnlyProperty<?>, P>
                extends SQLMappingAdapter<T, P>
        {
            /*
            public EnumSQLMappingAdapter(final Class<T> type, final Class<P> primitive)
            {
                super(type, primitive);
            }
            */

            // FIXME fix this raw class hack
            @SuppressWarnings("unchecked")
            public EnumSQLMappingAdapter(final Class type, final Class primitive)
            {
                super(type, primitive);
            }

            protected <T extends Enum<T>> Class<T> getEnum(final MappingMetaData metaData)
            {
                return FrameworkServiceImpl.getEntityService().requestEnumForName(metaData.getAlias());
            }
        }

        final MappingAdapterHolder<ResultSet, ServerStorageStructure, ReadOnlyProperty<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // String
            .registerAdapter(new SQLMappingAdapter<StringProperty, String>(StringProperty.class, String.class)
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
                    protected boolean setAdaptedType(final StringProperty me, final String value)
                    {
                        me.set(value);
                        return true;
                    }
                    })
                 // FloatProperty
                .registerAdapter(new SQLMappingAdapter<FloatProperty, Float>(FloatProperty.class, Float.class)
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
                    protected boolean setAdaptedType(final FloatProperty me, final Float value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // DoubleProperty
            .registerAdapter(new SQLMappingAdapter<DoubleProperty, Double>(DoubleProperty.class, Double.class)
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
                    protected boolean setAdaptedType(final DoubleProperty me, final Double value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // BooleanProperty
            .registerAdapter(new SQLMappingAdapter<BooleanProperty, Boolean>(BooleanProperty.class, Boolean.class)
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
                    protected boolean setAdaptedType(final BooleanProperty me, final Boolean value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // IntegerProperty
            .registerAdapter(new SQLMappingAdapter<IntegerProperty, Integer>(IntegerProperty.class, Integer.class)
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
                    protected boolean setAdaptedType(final IntegerProperty me, final Integer value)
                    {
                        me.set(value);
                        return true;
                    }
                })
            // ReadOnlyIntegerProperty
            .registerAdapter(new SQLMappingAdapter<ReadOnlyIntegerProperty, Integer>(ReadOnlyIntegerProperty.class, Integer.class)
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
                })
             // EnumProperty
             .registerAdapter(new EnumSQLMappingAdapter<EnumProperty<? extends Enum<?>>, Enum<?>>(EnumProperty.class, Enum.class)
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

                    @SuppressWarnings("unchecked")
                    @Override
                    protected boolean setAdaptedType(final EnumProperty<? extends Enum<?>> me, Enum<?> value)
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
              .registerAdapter(new EnumSQLMappingAdapter<FlagProperty<? extends Enum<?>>, Integer>(FlagProperty.class, Integer.class)
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
