
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapter;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.framework.internal.storage.mapping.MappingPlan;
import com.github.naios.wide.framework.internal.storage.mapping.types.EnumProperty;
import com.github.naios.wide.framework.internal.storage.mapping.types.FlagProperty;
import com.github.naios.wide.framework.internal.storage.mapping.types.ReadOnlyEnumProperty;
import com.github.naios.wide.framework.internal.storage.mapping.types.ReadOnlyFlagProperty;
import com.github.naios.wide.framework.internal.storage.server.AliasUtil;
import com.google.common.reflect.TypeToken;

public class ClientStorageRecordToPropertyMappingAdapterHolder
{
    public final static MappingAdapterHolder<ClientStorageRecord, ?, ObservableValue<?>> INSTANCE = build();

    private static MappingAdapterHolder<ClientStorageRecord, ?, ObservableValue<?>> build()
    {
        final MappingAdapterHolder<ClientStorageRecord, ?, ObservableValue<?>> holder =
                new MappingAdapterHolder<>();

        holder
            // Integer
            .registerAdapter(TypeToken.of(ReadOnlyIntegerProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyIntegerProperty>()
                {
                    @Override
                    public ReadOnlyIntegerProperty map(
                            final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new ReadOnlyIntegerWrapper(from.getInt(metaData.getIndex(), metaData.isKey()));
                    }

                    @Override
                    public ReadOnlyIntegerProperty create(
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return new ReadOnlyIntegerWrapper();
                    }

                    @Override
                    public boolean isPossibleKey()
                    {
                        return true;
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyIntegerProperty me)
                    {
                        return me.get();
                    }
                })
            // Long
            .registerAdapter(TypeToken.of(ReadOnlyLongProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyLongProperty>()
                {
                    @Override
                    public ReadOnlyLongProperty map(
                            final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new ReadOnlyLongWrapper(from.getLong(metaData.getIndex()));
                    }

                    @Override
                    public ReadOnlyLongProperty create(
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return new ReadOnlyLongWrapper();
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyLongProperty me)
                    {
                        return me.get();
                    }
                })
            // String
            .registerAdapter(TypeToken.of(ReadOnlyStringProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyStringProperty>()
                {
                    @Override
                    public ReadOnlyStringProperty map(
                            final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new ReadOnlyStringWrapper(from.getString(metaData.getIndex()));
                    }

                    @Override
                    public ReadOnlyStringProperty create(
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return new ReadOnlyStringWrapper();
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyStringProperty me)
                    {
                        return me.get();
                    }
                })
             // FloatProperty
            .registerAdapter(TypeToken.of(ReadOnlyFloatProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyFloatProperty>()
                {
                    @Override
                    public ReadOnlyFloatProperty map(final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new ReadOnlyFloatWrapper(from.getFloat(metaData.getIndex()));
                    }

                    @Override
                    public ReadOnlyFloatProperty create(final MappingPlan plan,
                            final int index, final MappingMetaData metaData, final Object value)
                    {
                        return new ReadOnlyFloatWrapper();
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyFloatProperty me)
                    {
                        return me.get();
                    }
                })
            // Enum Property
            .registerAdapter(TypeToken.of(ReadOnlyEnumProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyEnumProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public ReadOnlyEnumProperty map(final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new EnumProperty(AliasUtil.getEnum(metaData.getAlias()), from.getInt(metaData.getIndex()));
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public ReadOnlyEnumProperty<?> create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new EnumProperty(AliasUtil.getEnum(metaData.getAlias())), value);
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyEnumProperty<?> me)
                    {
                        return me.getValue().intValue();
                    }
                })
            // Flag Property
            .registerAdapter(TypeToken.of(ReadOnlyFlagProperty.class), new MappingAdapter<ClientStorageRecord, ReadOnlyFlagProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public ReadOnlyFlagProperty map(final ClientStorageRecord from,
                            final MappingPlan plan, final int index,
                            final MappingMetaData metaData)
                    {
                        return new FlagProperty(AliasUtil.getEnum(metaData.getAlias()), from.getInt(metaData.getIndex()));
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public ReadOnlyFlagProperty<?> create(final MappingPlan plan, final int index,
                            final MappingMetaData metaData, final Object value)
                    {
                        return createHelper(new FlagProperty(AliasUtil.getEnum(metaData.getAlias())), value);
                    }

                    @Override
                    public Object getRawHashableValue(final ReadOnlyFlagProperty<?> me)
                    {
                        return me.getValue().intValue();
                    }
                });

        return holder;
    }
}
