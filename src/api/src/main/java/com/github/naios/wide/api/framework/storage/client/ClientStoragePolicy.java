
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api.framework.storage.client;

public enum ClientStoragePolicy
{
    /**
     * Use a provided schema only, non-existing schema will throw an exception
     */
    POLICY_SCHEMA_ONLY,

    /**
     * Estimate table schema only, format will be imprecise for complex structures<br>
     * Only UnknownClientStorageStructure can be mapped.
     */
    POLICY_ESTIMATE_ONLY,

    /**
     * Try to get a provided schema first, if none were found estimate format. <b>(default policy)</b><br>
     */
    POLICY_SCHEMA_FIRST_ESTIMATE_AFTER;

    public static ClientStoragePolicy DEFAULT_POLICY = POLICY_SCHEMA_FIRST_ESTIMATE_AFTER;

    public boolean isSchemaProvided()
    {
        return equals(POLICY_SCHEMA_ONLY) ||
               equals(POLICY_SCHEMA_FIRST_ESTIMATE_AFTER);
    }

    public boolean isSchemaEstimated()
    {
        return equals(POLICY_ESTIMATE_ONLY) ||
               equals(POLICY_SCHEMA_FIRST_ESTIMATE_AFTER);
    }
}
