
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.StringUtil;

public class SQLVariableHolder
{
    private final Map<String, String> variables = new HashMap<>();

    protected final static String PREFIX_DELIMITER = "_";

    protected final static String PREFIX_NONE = "";

    public String addVariable(final String id, final Object value)
    {
        return addVariable(StringUtil.convertStringToVarName(id), value, 1);
    }

    private String addVariable(final String id, final Object value, final int run)
    {
        final String svalue = new FormatterWrapper(value,
                FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX, FormatterWrapper.Options.NO_HEX_AND_BIN_ENCLOSE).toString();
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
        if (variables.isEmpty())
            return;

        final Set<SQLVariable> orderedVariables = new TreeSet<>();

        // Get the max length (used to format the var declaration)
        int varNameMaxLength = 0, varValueMaxLength = 0;
        for (final Entry<String, String> entry : variables.entrySet())
        {
            varNameMaxLength = Math.max(entry.getKey().length(), varNameMaxLength);
            varValueMaxLength = Math.max(entry.getValue().length(), varValueMaxLength);

            orderedVariables.add(new SQLVariable(entry.getKey(), entry.getValue()));
        }

        final String format = SQLMaker.createVariableFormat(varNameMaxLength, varValueMaxLength);

        String prefix = PREFIX_NONE;

        // Orders variables by name
        for (final SQLVariable var : orderedVariables)
        {
            // Inserts newlines after prefix changed, useful to group enums or namestorage declarations
            if (!var.getPrefix().equals(prefix))
            {
                prefix = var.getPrefix();
                writer.println();
            }

            writer.println(SQLMaker.createVariable(format, var.getName(), var.getValue()));
        }

        writer.println();
    }
}
