
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
import java.util.Map.Entry;

import com.github.naios.wide.core.framework.util.FormatterWrapper;

public class SQLVariableHolder
{
    private final Map<String, String> variables = new HashMap<>();

    private String addVariable(final String id, final Object value)
    {
        return addVariable(id, value, 1);
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
        // TODO Sort variables
        for (final Entry<String, String> entry : variables.entrySet())
            writer.println(String.format("SET %s := %s;", entry.getKey(), entry.getValue()));
    }
}
