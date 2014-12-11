
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server.builder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.Pair;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.core.framework.util.FormatterWrapper.Options;

public class SQLMaker
{
    public static final String DELEMITER = ";";

    public static final String SPACE = " ";

    public static final String WHERE = "WHERE";

    public static final String UPDATE = "UPDATE";

    public static final String DELETE = "DELETE";

    public static final String SET = "SET";

    public static final String OR = "OR";

    public static final String AND = "AND";

    public static final String IN = "IN";

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
     * Creates a sql in clause.
     */
    public static String createInClause(final String query)
    {
        return IN + "(" + query + ")";
    }

    /**
     * Creates field equals value clause.
     */
    public static String createFieldEqualsValue(final SQLVariableHolder vars, final Field field, final ObservableValue<?> value)
    {
        // TODO implement variables here
        return StringUtil.fillWithSpaces(field.getName(), EQUAL, new FormatterWrapper(value.getValue(), FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX));
    }

    /**
     * creates the key part of an structure
     */
    public static String createKeyPart(final SQLVariableHolder vars, final ServerStorageStructure... structures)
    {
        if (structures.length == 0)
            return "";

        final List<Field> keys = structures[0].getPrimaryFields();

        // If only 1 primary key exists its possible to use IN clauses
        // otherwise we use nestes AND/ OR clauses
        if (keys.size() == 1 && (structures.length > 1))
        {
            return keys.get(0)
                    + createInClause(StringUtil.concat(", ",
                            new Iterator<String>()
                            {
                                int i = 0;

                                @Override
                                public boolean hasNext()
                                {
                                    return i < structures.length;
                                }

                                @Override
                                public String next()
                                {
                                    return structures[i].getKey().get()[0].toString();
                                }
                            }));
        }
        else
        {
            // Yay, nested concat iterator!
            return StringUtil.concat(SPACE + OR + SPACE, new Iterator<String>()
            {
                private int strucI = 0;

                @Override
                public boolean hasNext()
                {
                    return strucI < structures.length;
                }

                @Override
                public String next()
                {
                    ++strucI;
                    return "(" + StringUtil.concat(SPACE + AND + SPACE,
                            new Iterator<String>()
                            {
                                private int keyI = 0;

                                @Override
                                public boolean hasNext()
                                {
                                    return keyI < keys.size();
                                }

                                @Override
                                public String next()
                                {
                                    ++keyI;

                                    final ObservableValue<?> value;
                                    try
                                    {
                                        final Field field = keys.get(keyI - 1);

                                        if (!field.isAccessible())
                                            field.setAccessible(true);

                                        value = (ObservableValue<?>)field.get(structures[strucI - 1]);
                                    } catch (final IllegalArgumentException e)
                                    {
                                        e.printStackTrace();
                                        return "";
                                    } catch (final IllegalAccessException e)
                                    {
                                        e.printStackTrace();
                                        return "";
                                    }

                                    return createFieldEqualsValue(vars, keys.get(keyI - 1), value);
                                }
                            }) + ")";
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
            statements.add(createFieldEqualsValue(vars, value.second().getField(), value.first()));

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
