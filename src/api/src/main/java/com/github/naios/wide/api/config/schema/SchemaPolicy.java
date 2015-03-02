
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.config.schema;

public enum SchemaPolicy
{
    /**
     * Use the schema in config only
     */
    STRICT(),

    /**
     * Complete information to existing schemas, such as keys, order and default values
     * through mysql table info.
     */
    COMPLETE()
    {
        @Override
        public boolean hasPermissionToComplete()
        {
            return true;
        }
    },

    /**
     * Try to add as much info as possible to the MappingMetaData,
     * this includes adding of new columns, keys, order and default values.
     */
    LAZY()
    {
        @Override
        public boolean hasPermissionToComplete()
        {
            return true;
        }

        @Override
        public boolean hasPermissionToAddColumns()
        {
            return true;
        }
    };

    public boolean hasPermissionToComplete()
    {
        return false;
    }

    public boolean hasPermissionToAddColumns()
    {
        return false;
    }
}
