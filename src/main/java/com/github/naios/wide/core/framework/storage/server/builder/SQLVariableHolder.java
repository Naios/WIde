
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.StringUtil;

public class SQLVariableHolder
{
    private final Map<String, String> variables = new HashMap<>();

    private final static String PREFIX_DELIMITER = "_";

    private final static String PREFIX_NONE = "";

    public String addVariable(final String id, final Object value)
    {
        return addVariable(StringUtil.convertStringToVarName(id), value, 1);
    }

    private String addVariable(final String id, final Object value, final int run)
    {
        final String svalue = new FormatterWrapper(value, FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString();
        final String sid = (run == 1) ? String.format("@%s", id) : String.format("@%s_V%s", id, run);

        // If the variable is already contained in the variables with a different value rename it to id + "_V" + run
        final String containing_value = variables.get(sid);
        if (containing_value != null)
        {
            if (containing_value.equals(svalue))
                return sid;
            else
                return addVariable(id, value, run + 1);
        }
        else
        {
            variables.put(sid, svalue);
            return sid;
        }
    }

    public void writeQuery(final PrintWriter writer)
    {
        final Set<String> keys = new TreeSet<String>(variables.keySet());

        String prefix = PREFIX_NONE;

        // Orders variables by name
        for (final String key : keys)
        {
            // Inserts newlines after prefix changed, useful to group enums or namestorage declarations
            final int prefixIdx = key.indexOf(PREFIX_DELIMITER);

            final String keyPrefix;
            if (prefixIdx == -1)
                keyPrefix = PREFIX_NONE;
            else
                keyPrefix = key.substring(0, prefixIdx);

            if (!keyPrefix.equals(prefix))
            {
                prefix = keyPrefix;
                writer.println();
            }

            writer.println(SQLMaker.createVariable(key, variables.get(key)));
        }
    }
}
