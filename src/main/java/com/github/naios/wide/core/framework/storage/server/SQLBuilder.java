package com.github.naios.wide.core.framework.storage.server;

import java.util.HashMap;
import java.util.Map;

import com.github.naios.wide.core.framework.util.FormatterWrapper;

public class SQLBuilder
{
    private static final SQLBuilder INSTANCE = new SQLBuilder();

    public SQLBuilder instance()
    {
        return INSTANCE;
    }

    private class LazyBuilder
    {
        private final Map<String /*id*/, String /*value*/> variables =
                new HashMap<>();

        private String makeStringToVarName(final String str)
        {
            return str
                    .toUpperCase()
                    .replace(" ", "_")
                    .replace("['@\"]", "");
        }

        public void addVariable(final String id, final Object value)
        {
            variables.put(makeStringToVarName(id), new FormatterWrapper(value, FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString());
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }

    public String build()
    {
        final StringBuilder builder = new StringBuilder();

        // Iterate through all changed ObservableValues

        // Replace namestorage, enum and flag fields through sql variables

        // Write "Changesets" for all updated ObservableValues & deleted structures

        // Summary update/delete Changesets that target the same table but different keys

        // Write insert querys for all new structures, summary same tables

        return builder.toString();
    }
}
