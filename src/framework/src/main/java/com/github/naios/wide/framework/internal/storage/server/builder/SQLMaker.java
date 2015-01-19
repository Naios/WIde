
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.StructureChangeTracker;
import com.github.naios.wide.api.util.CrossIterator;
import com.github.naios.wide.api.util.FlagUtil;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.entities.util.EnumProperty;
import com.github.naios.wide.entities.util.FlagProperty;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.google.common.collect.Iterables;

public class SQLMaker
{
    protected static final String DELEMITER = ";";

    protected static final String SPACE = " ";

    protected static final String COMMA = ",";

    protected static final String WHERE = "WHERE";

    protected static final String UPDATE = "UPDATE";

    protected static final String DELETE = "DELETE";

    protected static final String INSERT = "INSERT";

    protected static final String INTO = "INTO";

    protected static final String VALUES = "VALUES";

    protected static final String FROM = "FROM";

    protected static final String SET = "SET";

    protected static final String OR = "OR";

    protected static final String AND = "AND";

    protected static final String IN = "IN";

    protected static final String ASSIGN = ":=";

    protected static final String EQUAL = "=";

    protected static final String NAME_ENCLOSURE = "`";

    protected static final String FLAG_DELEMITER = " | ";

    protected static final String NEWLINE = "\n";

    /**
     * Adds the delemiter to a query
     */
    protected static String addDelemiter(final String query)
    {
        return query + DELEMITER;
    }

    /**
     * Creates sql single or multiline comments
     */
    public static String createComment(final String text)
    {
        if (text.contains(NEWLINE))
            return "/*\n * " + text.replaceAll(NEWLINE, "\n * ") + "\n */";
        else
            return "-- " + text;
    }

    /**
     * Creates a table name
     */
    protected static String createName(final String table)
    {
        return NAME_ENCLOSURE + table + NAME_ENCLOSURE;
    }

    protected static String createName(final MappingMetaData metaData)
    {
        return createName(metaData.getName());
    }

    protected static String createVariableFormat(final int varNameMaxLength, final int varValueMaxLength)
    {
        return " %-" + varNameMaxLength + "s := %" + varValueMaxLength + "s";
    }

    /**
     * Creates a sql variable.
     * @param varNameMaxLength
     */
    protected static String createVariable(final String format, final String name, final String value)
    {
        return addDelemiter(SET + String.format(format, name, value));
    }

    /**
     * Creates a sql in clause.
     */
    protected static String createInClause(final SQLVariableHolder vars, final StructureChangeTracker changeTracker,
            final MappingMetaData mappingMetaData, final Collection<ServerStorageStructure> structures)
    {
        final String query = StringUtil.concat(COMMA + SPACE,
                new CrossIterator<>(structures, structure ->
                {
                    final Pair<ObservableValue<?>, MappingMetaData> field = structure.getKeys().get(0);
                    return createValueOfObservableValue(vars, changeTracker, structure, field, true);
                }));

        return createName(mappingMetaData) + SPACE + IN + "(" + query + ")";
    }

    /**
     * Creates name equals value clause.
     */
    protected static String createNameEqualsName(final String name, final String value)
    {
        return StringUtil.fillWithSpaces(name, EQUAL, value);
    }

    /**
     * Creates field equals value clause.
     */
    protected static String createNameEqualsValue(final SQLVariableHolder vars, final StructureChangeTracker changeTracker,
            final ServerStorageStructure structure, final Pair<ObservableValue<?>, MappingMetaData> field, final boolean variablize)
    {
        return createNameEqualsName(createName(field.second()), createValueOfObservableValue(vars, changeTracker, structure, field, variablize));
    }

    @SuppressWarnings({ "rawtypes" })
    private static String createValueOfObservableValue(final SQLVariableHolder vars, final StructureChangeTracker changeTracker,
            final ServerStorageStructure structure, final Pair<ObservableValue<?>, MappingMetaData> field, final boolean variablize)
    {
        if (variablize)
        {
            // If the observable has a custom var use it
            final String customVar = changeTracker.getCustomVariable(field.first());
            if (customVar != null)
                return vars.addVariable(customVar, field.first().getValue());

            // Enum alias
            if ((field.first() instanceof EnumProperty || field.first() instanceof FlagProperty)
                    && !field.second().getAlias().isEmpty())
            {
                final Class<? extends Enum> enumeration = FrameworkServiceImpl.getEntityService().requestEnumForName(field.second().getAlias());

                // Enum Property (Absolute value)
                if (field.first() instanceof EnumProperty)
                    return vars.addVariable(enumeration.getEnumConstants()[(int)field.first().getValue()].name(), field.first().getValue());
                // Flag Property (Relative value)
                else if (field.first() instanceof FlagProperty)
                {
                    // FlagProperties only occur in set statements
                    final int currentFlagValue = ((FlagProperty) field.first()).get();
                    final int oldFlagValue;
                    final Object oldValue = changeTracker.getRemoteValue(structure, field);
                    if (oldValue == null || !(oldValue instanceof Integer))
                        oldFlagValue = 0;
                    else
                        oldFlagValue = (int) oldValue;

                    // Get flag values of flags in database and in the current observable
                    final List<? extends Enum> currentFlags = FlagUtil.getFlagList(enumeration, currentFlagValue);
                    final List<? extends Enum> oldFlags = FlagUtil.getFlagList(enumeration, oldFlagValue);

                    // Now we calculate the difference
                    // Add Flags:
                    final List<Enum> addFlags = new ArrayList<>();
                    addFlags.addAll(currentFlags);
                    addFlags.removeAll(oldFlags);

                    // Remove Flags:
                    final List<Enum> removeFlags = new ArrayList<>();
                    removeFlags.addAll(oldFlags);
                    removeFlags.removeAll(currentFlags);

                    if ((!addFlags.isEmpty()) || (!removeFlags.isEmpty()))
                    {
                        final StringBuilder builder = new StringBuilder();

                        if (!removeFlags.isEmpty())
                            builder.append("(");

                        if (!oldFlags.isEmpty())
                            builder.append(createName(field.second()));

                        if (!removeFlags.isEmpty())
                        {
                            builder.append(" &~ (");

                            builder.append(concatFlags(vars, removeFlags));

                            builder.append("))");
                        }

                        if (!addFlags.isEmpty())
                        {
                            if (!oldFlags.isEmpty())
                                builder.append(FLAG_DELEMITER);

                            builder.append(concatFlags(vars, addFlags));
                        }

                        return builder.toString();
                    }
                    else if (currentFlags.isEmpty())
                        return String.valueOf(FlagUtil.DEFAULT_VALUE);
                    else // If Values are not different
                        return concatFlags(vars, currentFlags);
                }
            }
            // Namestorage alias
            else if ((field.first() instanceof ReadOnlyIntegerProperty) && !field.second().getAlias().isEmpty())
            {
                final String name = FrameworkServiceImpl.getInstance().requestAlias(field.second().getAlias(), (int)field.first().getValue());
                if (name != null)
                    return vars.addVariable(name, field.first().getValue());
            }
        }

        return new FormatterWrapper(field.first().getValue(), FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString();
    }

    /**
     * Helper to concat a list of flags as variables
     */
    @SuppressWarnings({ "rawtypes" })
    private static String concatFlags(final SQLVariableHolder vars, final List<? extends Enum> currentFlags)
    {
        return StringUtil.concat(FLAG_DELEMITER, new Iterator<String>()
        {
            int i = 0;

            @Override
            public boolean hasNext()
            {
                return i < currentFlags.size();
            }

            @Override
            public String next()
            {
                return vars.addVariable(currentFlags.get(i).name(),
                        StringUtil.asHex(FlagUtil.createFlag(currentFlags.get(i++))));
            }
        });
    }

    /**
     * creates the key part of an structure
     */
    protected static String createKeyPart(final SQLVariableHolder vars, final StructureChangeTracker changeTracker, final Collection<ServerStorageStructure> structures)
    {
        if (structures.size() == 0)
            return "";

        final List<Pair<ObservableValue<?>, MappingMetaData>> keys = Iterables.get(structures, 0).getKeys();

        // If only 1 primary key exists its possible to use IN clauses
        // otherwise we use nested AND/ OR clauses
        if (keys.size() == 1 && (structures.size() > 1))
            return createInClause(vars, changeTracker, keys.get(0).second(), structures);
        else
        {
            // Yay, nested concat iterator!
            return StringUtil.concat(SPACE + OR + SPACE,
                    new CrossIterator<ServerStorageStructure, String>(structures, structure ->
                    {
                        return StringUtil.concat(SPACE + AND + SPACE, new CrossIterator<>(structure.getKeys(), field ->
                        {
                            return createNameEqualsValue(vars, changeTracker, structure, field, true);
                        }));
                    }));
        }
    }

    /**
     * Creates only the update fields part of a collection containing observables with storage infos
     */
    protected static String createUpdateFields(final SQLVariableHolder vars, final StructureChangeTracker changeTracker,
            final ServerStorageStructure structure, final Collection<Pair<ObservableValue<?>, MappingMetaData>> fields)
    {
        final Set<String> statements = new TreeSet<>();
        fields.forEach(field -> statements.add(createNameEqualsValue(vars, changeTracker, structure, field, true)));

        return StringUtil.concat(COMMA + SPACE, statements.iterator());
    }

    /**
     * Creates an update query from table name, updateFields and keyPart.
     */
    protected static String createUpdateQuery(final String tableName, final String updateFields, final String keyPart)
    {
        return addDelemiter(StringUtil.fillWithSpaces(UPDATE, createName(tableName), SET, updateFields, WHERE, keyPart));
    }

    /**
     * Creates an delete query from table name and keyPart.
     */
    public static String createDeleteQuery(final String tableName, final String keyPart)
    {
        return addDelemiter(StringUtil.fillWithSpaces(DELETE, FROM, createName(tableName), WHERE, keyPart));
    }

    private static String createInsertHeaderPart(final String tableName, final List<Pair<ObservableValue<?>, MappingMetaData>> list)
    {
        return StringUtil.fillWithSpaces(INSERT, INTO, createName(tableName), createInsertDeclareValuesPart(list), VALUES);
    }

    private static String createInsertDeclareValuesPart(final List<Pair<ObservableValue<?>, MappingMetaData>> list)
    {
        return "(" + StringUtil.concat(COMMA + SPACE,
                new CrossIterator<Pair<ObservableValue<?>, MappingMetaData>, String>(list,
                        (entry) -> createName(entry.second().getName()))) + ")";
    }

    public static String createInsertValuePart(final SQLVariableHolder vars, final StructureChangeTracker changeTracker,
            final Collection<ServerStorageStructure> structures)
    {
        return StringUtil.concat(COMMA + NEWLINE,
                new CrossIterator<ServerStorageStructure, String>(structures, (structure) ->
                {
                    return "(" + StringUtil.concat(COMMA + SPACE,
                            new CrossIterator<Pair<ObservableValue<?>, MappingMetaData>, String>(structure,
                                    field -> createValueOfObservableValue(vars, changeTracker, structure, field, true))) + ")";
                }));
    }

    public static String createInsertQuery(final String tableName, final List<Pair<ObservableValue<?>, MappingMetaData>> list, final String valuePart)
    {
        return addDelemiter(StringUtil.fillWithNewLines(createInsertHeaderPart(tableName, list), valuePart));
    }
}
