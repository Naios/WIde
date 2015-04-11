
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;

import com.github.naios.wide.api.config.main.QueryTypeConfig;
import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.server.SQLUpdateInfo;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.property.EnumProperty;
import com.github.naios.wide.api.property.EnumPropertyBase;
import com.github.naios.wide.api.property.FlagProperty;
import com.github.naios.wide.api.property.ReadOnlyEnumProperty;
import com.github.naios.wide.api.property.ReadOnlyFlagProperty;
import com.github.naios.wide.api.util.Flags;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.framework.internal.storage.server.SQLUpdateInfoImpl;
import com.google.common.collect.Iterables;

public final class SQLMaker
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

    private final SQLBuilderImpl builder;

    private final SQLVariableHolder vars;

    private final QueryTypeConfig queryConfig;

    public SQLMaker(final SQLBuilderImpl builder, final QueryTypeConfig queryConfig)
    {
        this.vars = builder.getVariableHolder();
        this.builder = builder;
        this.queryConfig = queryConfig;
    }

    /**
     * Adds the delemiter to a query
     */
    protected static String addDelemiter(final String query)
    {
        return query + DELEMITER + NEWLINE;
    }

    /**
     * Creates sql single or multiline comments
     */
    public static String createComment(final String text)
    {
        if (text.contains(NEWLINE))
            return "/* *\n * " + text.replaceAll(NEWLINE, "\n * ") + "\n */";
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
        final String query = structures
                .stream()
                .map(structure -> createValueOfReadOnlyProperty(structure, new SQLUpdateInfoImpl(structure.getKeys().get(0))))
                .collect(Collectors.joining(COMMA + SPACE));

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
    private String createNameEqualsValue(final ServerStorageStructure structure, final SQLUpdateInfo field)
    {
        return createNameEqualsName(createName(MappingBeans.getMetaData(field.getProperty())),
                createValueOfReadOnlyProperty(structure, field));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String createValueOfReadOnlyProperty(final ServerStorageStructure structure, final SQLUpdateInfo sqlUpdateInfo)
    {
        // If the observable has a custom var use it
        if (queryConfig.custom().get())
        {
            final String customVar = builder.getSQLInfoProvider().getCustomVariable(structure, sqlUpdateInfo.getProperty());
            if (customVar != null)
                return vars.addVariable(customVar, sqlUpdateInfo.getProperty().getValue());
        }

        final MappingMetaData metaData = MappingBeans.getMetaData(sqlUpdateInfo.getProperty());

        // Enum alias
        if ((((sqlUpdateInfo.getProperty() instanceof EnumProperty) && queryConfig.enums().get())
                || ((sqlUpdateInfo.getProperty() instanceof FlagProperty) && queryConfig.flags().get()))
                && !metaData.getAlias().isEmpty())
        {
            final EnumPropertyBase<?> base = (EnumPropertyBase<?>) sqlUpdateInfo.getProperty();

            // Enum Property (Absolute value)
            if (base instanceof ReadOnlyEnumProperty)
            {
                final ReadOnlyEnumProperty<? extends Enum<?>> enumProperty = ((ReadOnlyEnumProperty<? extends Enum<?>>)base);
                return vars.addVariable(enumProperty.getValue().name(), enumProperty.getOrdinal());
            }
            // Flag Property (Relative value)
            else if (base instanceof ReadOnlyFlagProperty)
            {
                final ReadOnlyFlagProperty<? extends Enum<?>> flagProperty = (ReadOnlyFlagProperty<? extends Enum<?>>)base;

                // FlagProperties only occur in set statements
                final int newMask = flagProperty.getValue().intValue();

                final int oldMask;
                if (!sqlUpdateInfo.oldValueProperty().get().isPresent()|| !((sqlUpdateInfo.oldValueProperty().get().get()) instanceof Integer))
                    oldMask = 0;
                else
                    oldMask = (int) sqlUpdateInfo.oldValueProperty().get().get();

                // Now we calculate the difference
                // Add Flags:
                final List<? extends Enum<?>> addFlags = new ArrayList<>();
                // Remove Flags:
                final List<? extends Enum<?>> removeFlags = new ArrayList<>();

                // FIXME Remove raw class hack
                Flags.calculateDifferenceTo(flagProperty.getEnumClass(), oldMask, newMask, (List)addFlags, (List)removeFlags);

                if ((!addFlags.isEmpty()) || (!removeFlags.isEmpty()))
                {
                    final StringBuilder builder = new StringBuilder();

                    if (!removeFlags.isEmpty())
                        builder.append("(");

                    if (oldMask != 0)
                        builder.append(createName(metaData));

                    if (!removeFlags.isEmpty())
                    {
                        builder.append(" &~ (");

                        builder.append(concatFlags((List)removeFlags));

                        builder.append("))");
                    }

                    if (!addFlags.isEmpty())
                    {
                        if (oldMask != 0)
                            builder.append(FLAG_DELEMITER);

                        builder.append(concatFlags((List)addFlags));
                    }

                    return builder.toString();
                }
                else if (newMask == 0)
                    return String.valueOf(Flags.DEFAULT_VALUE);
                else
                {
                    final Set<? extends Enum<?>> currentFlags = Flags.flagSet(flagProperty.getEnumClass(), oldMask);
                    // If Values are not different
                    return concatFlags((Collection)currentFlags);
                }
            }
        }
        // Namestorage alias
        else if ((sqlUpdateInfo.getProperty() instanceof ReadOnlyIntegerProperty)
                    && queryConfig.alias().get()
                        && !metaData.getAlias().isEmpty())
        {
            final ReadOnlyIntegerProperty integerProperty = (ReadOnlyIntegerProperty) sqlUpdateInfo.getProperty();

            final String name = builder.getWorkspace()
                    .requestAlias(metaData.getAlias(), integerProperty.get());

            if (name != null)
                return vars.addVariable(name, sqlUpdateInfo.getProperty().getValue());
        }

        return new FormatterWrapper(sqlUpdateInfo.getProperty().getValue(),
                FormatterWrapper.Options.NO_FLOAT_DOUBLE_POSTFIX).toString();
    }

    /**
     * Helper to concat a list of flags as variables
     */
    private String concatFlags(final Collection<Enum<?>> flags)
    {
        return flags
                .stream()
                .map(flag -> vars.addVariable(flag.name(), StringUtil.asHex(Flags.createFlag(flag))))
                .collect(Collectors.joining(FLAG_DELEMITER));
    }

    /**
     * creates the key part of an structure
     */
    protected String createKeyPart(final Collection<ServerStorageStructure> structures)
    {
        if (structures.size() == 0)
            return "";

        final List<ReadOnlyProperty<?>> keys = Iterables.get(structures, 0).getKeys();

        // If only 1 primary key exists its possible to use IN clauses
        // otherwise we use nested AND/ OR clauses
        if (keys.size() == 1 && (structures.size() > 1))
            return createInClause(MappingBeans.getMetaData(keys.get(0)), structures);
        else
        {
            return structures
                    .stream()
                    .map(structure -> structure.getKeys()
                            .stream()
                            .map(field -> createNameEqualsValue(structure, new SQLUpdateInfoImpl(field)))
                            .collect(Collectors.joining(SPACE + AND + SPACE)))
                    .collect(Collectors.joining(SPACE + OR + SPACE));
        }
    }

    /**
     * Creates only the update fields part of a collection containing observables with storage infos
     */
    protected String createUpdateFields(final ServerStorageStructure structure, final Collection<SQLUpdateInfo> collection)
    {
        final Set<String> statements = new TreeSet<>();
        collection.forEach(field -> statements.add(createNameEqualsValue(structure, field)));

        return statements.stream().collect(Collectors.joining(COMMA + SPACE));
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

    private static String createInsertHeaderPart(final String tableName, final List<ReadOnlyProperty<?>> list)
    {
        return INSERT + SPACE + INTO + createName(tableName) + NEWLINE + SPACE + createInsertDeclareValuesPart(list) + NEWLINE + VALUES;
    }

    private static String createInsertDeclareValuesPart(final List<ReadOnlyProperty<?>> list)
    {
        return list
                .stream()
                .map(MappingBeans::getMetaData)
                .map(SQLMaker::createName)
                .collect(Collectors.joining(COMMA + SPACE, "(", ")"));
    }

    protected String createInsertValuePart(final Collection<ServerStorageStructure> structures)
    {
        return structures
                .stream()
                .map(structure -> structure
                        .stream()
                        .map(field -> createValueOfReadOnlyProperty(structure, new SQLUpdateInfoImpl(field)))
                        .collect(Collectors.joining(COMMA + SPACE, " (", ")")))
                .collect(Collectors.joining(COMMA + NEWLINE));
    }

    protected String createInsertQuery(final String tableName, final List<ReadOnlyProperty<?>> list, final String valuePart)
    {
        return addDelemiter(StringUtil.fillWithNewLines(createInsertHeaderPart(tableName, list), valuePart));
    }
}
