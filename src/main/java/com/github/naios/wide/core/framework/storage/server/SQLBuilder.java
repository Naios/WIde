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

        public void addVariable(final String id, final Object value)
        {
            variables.put(id, new FormatterWrapper(value, FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString());
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








        return builder.toString();
    }
}
