
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
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
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

    private final SQLBuilder builder;

    private final SQLVariableHolder vars;

    public SQLMaker(final SQLBuilder builder, final SQLVariableHolder vars)
    {
        this.vars = vars;
        this.builder = builder;
    }

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
    private String createInClause(final MappingMetaData mappingMetaData, final Collection<ServerStorageStructure> structures)
    {
        final String query = StringUtil.concat(COMMA + SPACE,
                new CrossIterator<>(structures, structure ->
                {
                    final Pair<ObservableValue<?>, MappingMetaData> field = structure.getKeys().get(0);
                    return createValueOfObservableValue(structure, new SQLUpdateInfoImpl(field), true);
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
    private String createNameEqualsValue(final ServerStorageStructure structure, final SQLUpdateInfo field, final boolean variablize)
    {
        return createNameEqualsName(createName(field.getEntry().second()), createValueOfObservableValue(structure, field, variablize));
    }

    @SuppressWarnings({ "rawtypes" })
    private String createValueOfObservableValue(final ServerStorageStructure structure, final SQLUpdateInfo sqlUpdateInfo, final boolean variablize)
    {
        if (variablize)
        {
            // If the observable has a custom var use it
            final String customVar = builder.getSQLInfoProvider().getCustomVariable(structure, sqlUpdateInfo.getEntry());
            if (customVar != null)
                return vars.addVariable(customVar, sqlUpdateInfo.getEntry().first().getValue());

            // Enum alias
            if ((sqlUpdateInfo.getEntry().first() instanceof EnumProperty || sqlUpdateInfo.getEntry().first() instanceof FlagProperty)
                    && !sqlUpdateInfo.getEntry().second().getAlias().isEmpty())
            {
                final Class<? extends Enum> enumeration = FrameworkServiceImpl.getEntityService().requestEnumForName(sqlUpdateInfo.getEntry().second().getAlias());

                final ReadOnlyIntegerProperty integerProperty = (ReadOnlyIntegerProperty) sqlUpdateInfo.getEntry().first();

                // Enum Property (Absolute value)
                if (sqlUpdateInfo.getEntry().first() instanceof EnumProperty)
                    return vars.addVariable(enumeration.getEnumConstants()[integerProperty.get()].name(), sqlUpdateInfo.getEntry().first().getValue());
                // Flag Property (Relative value)
                else if (sqlUpdateInfo.getEntry().first() instanceof FlagProperty)
                {
                    // FlagProperties only occur in set statements
                    final int currentFlagValue = ((FlagProperty) sqlUpdateInfo.getEntry().first()).get();
                    final int oldFlagValue;

                    if (!sqlUpdateInfo.getOldValue().isPresent() ||
                        !((sqlUpdateInfo.getOldValue().get()) instanceof Integer))
                        oldFlagValue = 0;
                    else
                        oldFlagValue = (int) sqlUpdateInfo.getOldValue().get();

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
                            builder.append(createName(sqlUpdateInfo.getEntry().second()));

                        if (!removeFlags.isEmpty())
                        {
                            builder.append(" &~ (");

                            builder.append(concatFlags(removeFlags));

                            builder.append("))");
                        }

                        if (!addFlags.isEmpty())
                        {
                            if (!oldFlags.isEmpty())
                                builder.append(FLAG_DELEMITER);

                            builder.append(concatFlags(addFlags));
                        }

                        return builder.toString();
                    }
                    else if (currentFlags.isEmpty())
                        return String.valueOf(FlagUtil.DEFAULT_VALUE);
                    else // If Values are not different
                        return concatFlags(currentFlags);
                }
            }
            // Namestorage alias
            else if ((sqlUpdateInfo.getEntry().first() instanceof ReadOnlyIntegerProperty) && !sqlUpdateInfo.getEntry().second().getAlias().isEmpty())
            {
                final ReadOnlyIntegerProperty integerProperty = (ReadOnlyIntegerProperty) sqlUpdateInfo.getEntry().first();

                final String name = FrameworkServiceImpl.getInstance().requestAlias(sqlUpdateInfo.getEntry().second().getAlias(), integerProperty.get());
                if (name != null)
                    return vars.addVariable(name, sqlUpdateInfo.getEntry().first().getValue());
            }
        }

        return new FormatterWrapper(sqlUpdateInfo.getEntry().first().getValue(), FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString();
    }

    /**
     * Helper to concat a list of flags as variables
     */
    @SuppressWarnings({ "rawtypes" })
    private String concatFlags(final List<? extends Enum> currentFlags)
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
    protected String createKeyPart(final Collection<ServerStorageStructure> structures)
    {
        if (structures.size() == 0)
            return "";

        final List<Pair<ObservableValue<?>, MappingMetaData>> keys = Iterables.get(structures, 0).getKeys();

        // If only 1 primary key exists its possible to use IN clauses
        // otherwise we use nested AND/ OR clauses
        if (keys.size() == 1 && (structures.size() > 1))
            return createInClause(keys.get(0).second(), structures);
        else
        {
            // Yay, nested concat iterator!
            return StringUtil.concat(SPACE + OR + SPACE,
                    new CrossIterator<ServerStorageStructure, String>(structures, structure ->
                    {
                        return StringUtil.concat(SPACE + AND + SPACE, new CrossIterator<>(structure.getKeys(), field ->
                        {
                            return createNameEqualsValue(structure, new SQLUpdateInfoImpl(field), true);
                        }));
                    }));
        }
    }

    /**
     * Creates only the update fields part of a collection containing observables with storage infos
     */
    protected String createUpdateFields(final ServerStorageStructure structure, final Collection<SQLUpdateInfo> collection)
    {
        final Set<String> statements = new TreeSet<>();
        collection.forEach(field -> statements.add(createNameEqualsValue(structure, field, true)));

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

    protected String createInsertValuePart(final Collection<ServerStorageStructure> structures)
    {
        return StringUtil.concat(COMMA + NEWLINE,
                new CrossIterator<ServerStorageStructure, String>(structures, (structure) ->
                {
                    return "(" + StringUtil.concat(COMMA + SPACE,
                            new CrossIterator<Pair<ObservableValue<?>, MappingMetaData>, String>(structure,
                                    field -> createValueOfObservableValue(structure, new SQLUpdateInfoImpl(field), true))) + ")";
                }));
    }

    String createInsertQuery(final String tableName, final List<Pair<ObservableValue<?>, MappingMetaData>> list, final String valuePart)
    {
        return addDelemiter(StringUtil.fillWithNewLines(createInsertHeaderPart(tableName, list), valuePart));
    }
}
