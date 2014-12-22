
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.sql.SQLException;

import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
                })
            // Enum Property
            .registerAdapter(TypeToken.of(EnumProperty.class), new MappingAdapter<ClientStorageRecord, EnumProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public EnumProperty map(final ClientStorageRecord from,
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
            .registerAdapter(TypeToken.of(FlagProperty.class), new MappingAdapter<ClientStorageRecord, FlagProperty<?>>()
                {
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    public FlagProperty map(final ClientStorageRecord from,
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
