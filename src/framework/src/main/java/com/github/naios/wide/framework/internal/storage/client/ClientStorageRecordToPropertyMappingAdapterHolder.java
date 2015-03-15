
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.util.Optional;

import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.framework.storage.mapping.MappingPlan;
import com.github.naios.wide.api.property.ReadOnlyEnumProperty;
import com.github.naios.wide.api.property.ReadOnlyFlagProperty;
import com.github.naios.wide.api.property.SimpleEnumProperty;
import com.github.naios.wide.api.property.SimpleFlagProperty;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;

public class ClientStorageRecordToPropertyMappingAdapterHolder
{
    public final static MappingAdapterHolder<ClientStorageRecord, ?, ReadOnlyProperty<?>> INSTANCE = build();

    @SuppressWarnings("rawtypes")
    private static MappingAdapterHolder<ClientStorageRecord, ClientStorageStructure, ReadOnlyProperty<?>> build()
    {
        final MappingAdapterHolder<ClientStorageRecord, ClientStorageStructure, ReadOnlyProperty<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // String
            .registerAdapter(new ClientMetaDataMappingAdapter<ReadOnlyStringProperty, String>(ReadOnlyStringProperty.class, String.class)
                {
                    @Override
                    protected String getDefault(final MappingMetaData metaData)
                    {
                        return "";
                    }

                    @Override
                    public String getPrimitiveValue(final ReadOnlyStringProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected String getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getString(metaData.getIndex());
                    }

                    @Override
                    public ReadOnlyStringProperty create(final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<String> value)
                    {
                        return new SimpleStringProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault(metaData)));
                    }
                })
              // FloatProperty
                .registerAdapter(new ClientMetaDataMappingAdapter<ReadOnlyFloatProperty, Float>(ReadOnlyFloatProperty.class, Float.class)
                {
                    @Override
                    protected Float getDefault(final MappingMetaData metaData)
                    {
                        return 0.f;
                    }

                    @Override
                    public Float getPrimitiveValue(final ReadOnlyFloatProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected Float getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getFloat(metaData.getIndex());
                    }

                    @Override
                    public ReadOnlyFloatProperty create(final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Float> value)
                    {
                        return new SimpleFloatProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault(metaData)));
                    }
                })
             // Long Property
             .registerAdapter(new ClientMetaDataMappingAdapter<ReadOnlyLongProperty, Long>(ReadOnlyLongProperty.class, Long.class)
                {
                    @Override
                    protected Long getDefault(final MappingMetaData metaData)
                    {
                        return 0L;
                    }

                    @Override
                    public Long getPrimitiveValue(final ReadOnlyLongProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected Long getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getLong(metaData.getIndex());
                    }

                    @Override
                    public ReadOnlyLongProperty create(final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Long> value)
                    {
                        return new SimpleLongProperty(createBean(to, metaData), metaData.getName(), value.orElse(getDefault(metaData)));
                    }
                })
            // ReadOnlyIntegerProperty
            .registerAdapter(new ClientMetaDataMappingAdapter<ReadOnlyIntegerProperty, Integer>(ReadOnlyIntegerProperty.class, Integer.class)
                {
                    @Override
                    protected Integer getDefault(final MappingMetaData metaData)
                    {
                        return 0;
                    }

                    @Override
                    public Integer getPrimitiveValue(final ReadOnlyIntegerProperty me)
                    {
                        return me.get();
                    }

                    @Override
                    protected Integer getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getInt(metaData.getIndex());
                    }

                    @Override
                    public ReadOnlyIntegerProperty create(final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Integer> value)
                    {
                        return new ReadOnlyIntegerWrapper(createBean(to, metaData), metaData.getName(), value.orElse(getDefault(metaData)));
                    }
                })
             // EnumProperty
             .registerAdapter(new ClientEnumMetaDataMappingAdapter<ReadOnlyEnumProperty<? extends Enum<?>>, Enum<?>>(ReadOnlyEnumProperty.class, Enum.class)
                {
                    @Override
                    protected Enum<?> getDefault(final MappingMetaData metaData)
                    {
                        return null;
                    }

                    @Override
                    public Enum<?> getPrimitiveValue(final ReadOnlyEnumProperty<? extends Enum<?>> me)
                    {
                        return me.getValue();
                    }

                    private Enum<?> getDefaultForEnum(final MappingMetaData metaData)
                    {
                        return getEnum(metaData).getEnumConstants()[0];
                    }

                    @Override
                    protected Enum<?> getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        final Class<? extends Enum<?>> type = getEnum(metaData);

                        final int ordinal;
                        ordinal = from.getInt(metaData.getIndex());

                        if (ordinal >= type.getEnumConstants().length)
                            throw new IllegalArgumentException(String.format("Ordinal %s at column is not part in enum %s!", ordinal, metaData.getName(), type.getName()));

                        return type.getEnumConstants()[ordinal];
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public ReadOnlyEnumProperty<? extends Enum<?>> create(
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Enum<?>> value)
                    {
                        return new SimpleEnumProperty(getEnum(metaData), createBean(to, metaData), metaData.getName(), value.orElse(getDefaultForEnum(metaData)));
                    }
                })

                // FlagProperty
              .registerAdapter(new ClientEnumMetaDataMappingAdapter<ReadOnlyFlagProperty<? extends Enum<?>>, Integer>(ReadOnlyFlagProperty.class, Integer.class)
                {
                    @Override
                    protected Integer getDefault(final MappingMetaData metaData)
                    {
                        return 0;
                    }

                    @Override
                    public Integer getPrimitiveValue(final ReadOnlyFlagProperty<? extends Enum<?>> me)
                    {
                        return me.getValue().intValue();
                    }

                    @Override
                    protected Integer getMappedValue(final ClientStorageRecord from,
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return from.getInt(metaData.getIndex());
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public ReadOnlyFlagProperty<? extends Enum<?>> create(
                            final ClientStorageStructure to,
                            final MappingPlan<ReadOnlyProperty<?>> plan, final int index,
                            final MappingMetaData metaData, final Optional<Integer> value)
                    {
                        return new SimpleFlagProperty(getEnum(metaData), createBean(to, metaData), metaData.getName(), value.orElse(getDefault(metaData)));
                    }
                });

        return holder;
    }
}
