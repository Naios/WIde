
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.builder.SQLVariableHolder;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;

public class SQLUtil
{
    public static final String DELEMITER = ";";

    public static final String SPACE = " ";

    public static final String WHERE = "WHERE";

    public static final String UPDATE = "UPDATE";

    public static final String DELETE = "DELETE";

    public static final String SET = "SET";

    public static final String OR = "OR";

    public static final String AND = "AND";

    public static final String ASSIGN = ":=";

    public static final String EQUAL = "=";

    public static final String NAME_ENCLOSURE = "`";

    /**
     * Adds the delemiter to a query
     */
    public static String addDelemiter(final String query)
    {
        return query + DELEMITER;
    }

    /**
     * Creates sql single or multiline comments
     */
    public static String createComment(final String text)
    {
        if (text.contains("\n"))
            return "/*\n * " + text.replaceAll("\n", "\n * ") + "\n */";
        else
            return "-- " + text;
    }

    /**
     * Creates a table name
     */
    public static String createName(final String table)
    {
        return NAME_ENCLOSURE + table + NAME_ENCLOSURE;
    }

    /**
     * Creates a sql variable.
     */
    public static String createVariable(final String name, final String value)
    {
        return addDelemiter(StringUtil.fillWithSpaces(SET, name, ASSIGN, value));
    }

    /**
     * Creates field equals value clause.
     */
    public static String createFieldEqualsValue(final String field, final Object value)
    {
        return StringUtil.fillWithSpaces(field, EQUAL, new FormatterWrapper(value, FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX));
    }

    /**
     * creates the key part of an structure
     */
    public static String createKeyPart(final ServerStorageStructure... structures)
    {
        if (structures.length == 0)
            return "";

        final List<Field> fields = structures[0].getPrimaryFields();

        // If only 1 primary key exists its possible to use IN clauses
        // otherwise we use nestes AND/ OR clauses
        if (fields.size() == 1)
        {


            return "";
        }
        else
        {
            return StringUtil.concat(SPACE + OR + SPACE, new Iterator<String>()
            {
                int i = 0;

                @Override
                public boolean hasNext()
                {
                    return i < fields.size();
                }

                @Override
                public String next()
                {
                    return createFieldEqualsValue(fields.get(i).getName(), structures[i]);
                }
            });
        }
    }

    /**
     * Creates only the update fields part of a collection containing observables with storage infos
     */
    public static String createUpdateFields(final SQLVariableHolder vars,
            final Collection<Pair<ObservableValue<?>, ObservableValueStorageInfo>> fields)
    {
        final Set<String> statements = new TreeSet<>();

        for (final Pair<ObservableValue<?>, ObservableValueStorageInfo> value : fields)
            statements.add(createFieldEqualsValue(value.second().getField().getName(), value.first().getValue()));

        return StringUtil.concat(", ", statements.iterator());
    }

    /**
     * Creates an update querz from tablename, updateFields and keyPart.
     */
    public static String createUpdateQuery(final String tablename, final String updateFields, final String keyPart)
    {
        return addDelemiter(StringUtil.fillWithSpaces(UPDATE, createName(tablename), SET, updateFields, WHERE, keyPart));
    }
}
