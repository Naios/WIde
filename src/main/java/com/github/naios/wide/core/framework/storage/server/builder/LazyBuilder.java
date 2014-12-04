package com.github.naios.wide.core.framework.storage.server.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.IdentitySet;

/**
 * Implements several methods that helps us to build sql querys easier
 */
class LazyBuilder
{
    private final Map<String /*id*/, String /*value*/> variables =
            new HashMap<>();

    private final Collection<ServerStorageStructure> insert =
            new IdentitySet<>();

    private final Collection<ServerStorageStructure> delete =
            new IdentitySet<>();

    private final Collection<ObservableValue<?>> update =
            new IdentitySet<>();

    public String addVariable(final String id, final Object value)
    {


        return addVariable(id, value, 1);
    }

    public String addVariable(final String id, final Object value, final int run)
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

    public void insert(final ServerStorageStructure structure)
    {
        insert.add(structure);
    }

    public void delete(final ServerStorageStructure structure)
    {
        delete.add(structure);
    }

    public void update(final ObservableValue<?> value)
    {
        update.add(value);
    }

    private void writeVariables(final StringBuilder builder)
    {
        // TODO Sort variables
        for (final Entry<String, String> entry : variables.entrySet())
            builder.append(String.format("SET %s := %s;", entry.getKey(), entry.getValue()));
    }

    public String build()
    {
        final StringBuilder builder = new StringBuilder();
        writeVariables(builder);

        return builder.toString();
    }
}
