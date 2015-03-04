
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.AbstractMappingMetaData;
import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.SchemaPolicy;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageException;
import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.framework.storage.server.UnknownServerStorageStructure;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.github.naios.wide.framework.internal.storage.mapping.JsonMapper;
import com.github.naios.wide.framework.internal.storage.mapping.Mapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
class BadKeyException extends ServerStorageException
{
    public BadKeyException(final int givenKeyCount, final int structureKeyCount)
    {
        super(String.format("Count of passed keys {%s} does not match to the count of the structure {%s}!", givenKeyCount, structureKeyCount));
    }
}

@SuppressWarnings("serial")
class DatabaseConnectionException extends ServerStorageException
{
    public DatabaseConnectionException(final Throwable t)
    {
        super(String.format("Something went wrong with the database!"), t);
    }
}

@SuppressWarnings("serial")
class WrongDatabaseStructureException extends ServerStorageException
{
    public WrongDatabaseStructureException(final String name, final Throwable cause)
    {
        super(String.format("Your database structure dosn't match to %s!", name), cause);
    }
}

@SuppressWarnings("serial")
class StorageClosedException extends ServerStorageException
{
    public StorageClosedException()
    {
        super("Tried to access to this closed Storage!");
    }
}

@SuppressWarnings("serial")
class AccessedDeletedStructureException extends ServerStorageException
{
    public AccessedDeletedStructureException(final ServerStorageStructure structure)
    {
        super(String.format("Tried to access deleted Structure %s", structure));
    }
}

public class ServerStorageImpl<T extends ServerStorageStructure> implements ServerStorage<T>
{
    enum PreparedStatements
    {
        STATEMENT_SELECT_ROW
    }

    private final Cache<Integer /*hash*/, T /*entity*/> cache =
            CacheBuilder.newBuilder().weakValues().build();

    private final ObjectProperty<Database> database =
            new SimpleObjectProperty<>();

    private final BooleanProperty alive =
            new SimpleBooleanProperty();

    private final String databaseId;

    private final String statementFormat, selectLowPart, tableName;

    private final Mapper<ResultSet, T, ReadOnlyProperty<?>> mapper;

    private final String structureName;

    private final ChangeTrackerImpl changeTracker;

    public ServerStorageImpl(final String databaseId, final String tableName, final ChangeTrackerImpl changeTracker) throws ServerStorageException
    {
        this.databaseId = databaseId;
        this.tableName = tableName;
        this.changeTracker = changeTracker;

        this.database.bind(FrameworkServiceImpl.getDatabasePoolService()
                .requestConnection(databaseId));

        final Optional<TableSchema> trySchema = FrameworkServiceImpl.getConfigService().getActiveEnviroment()
                .getDatabaseConfig(databaseId).schema().get().getSchemaOf(tableName);

        // Estimate tables without provided schema
        // Create empty schema
        final TableSchema providedSchema = trySchema.orElseGet(() ->
        {
            return new TableSchema()
            {
                private final List<MappingMetaData> entries = Collections.emptyList();

                @Override
                public SchemaPolicy getPolicy()
                {
                    return SchemaPolicy.LAZY;
                }

                @Override
                public String getStructure()
                {
                    return UnknownServerStorageStructure.class.getCanonicalName();
                }

                @Override
                public String getName()
                {
                    return tableName;
                }

                @Override
                public ClientStorageFormat getFormat()
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public List<MappingMetaData> getEntries()
                {
                    return entries;
                }
            };
        });

        final TableSchema schema;
        final Function<MappingMetaData, Optional<TypeToken<?>>> typeReceiver;
        if (!providedSchema.getPolicy().hasPermissionToComplete())
        {
            schema = providedSchema;
            typeReceiver = metaData -> Optional.empty();
        }
        else
        {
            final Pair<TableSchema, Function<MappingMetaData, Optional<TypeToken<?>>>> result = estimateSchema(providedSchema);
            schema = result.first();
            typeReceiver = result.second();
        }

        this.structureName = schema.getStructure();

        mapper = new JsonMapper<>(schema, SQLToPropertyMappingAdapterHolder.<T>get(),
                Arrays.asList(ServerStorageStructurePrivateBase.class), ServerStorageStructureBaseImplementation.class, typeReceiver);

        selectLowPart = createSelectFormat();
        statementFormat = createStatementFormat();

        this.database.addListener(new ChangeListener<Database>()
        {
            @Override
            public void changed(final ObservableValue<? extends Database> observable,
                    final Database oldValue, final Database newValue)
            {
                alive.unbind();

                if (Objects.nonNull(newValue))
                {
                    alive.bind(newValue.alive());
                    registerStatements();
                }
                else
                    alive.set(false);
            }
        });

        registerStatements();
    }

    @Override
    public String getTableName()
    {
        return tableName;
    }

    @Override
    public String getDatabaseId()
    {
        return databaseId;
    }

    @Override
    public ChangeTracker getChangeTracker()
    {
        return changeTracker;
    }

    @Override
    public ReadOnlyBooleanProperty alive()
    {
        return alive;
    }

    private void checkOpen()
    {
        if (!alive().get())
            throw new StorageClosedException();
    }

    private String createSelectFormat()
    {
        return StringUtil.fillWithSpaces(
                "SELECT",
                mapper.getPlan().getMetadata()
                    .stream()
                    .map(MappingMetaData::getName).collect(Collectors.joining(", ")),
                "FROM", tableName, "WHERE ");
    }

    private String createStatementFormat()
    {
        return selectLowPart + mapper.getPlan().getKeys().stream().map(MappingMetaData::getName).map(name -> name  + "=?").collect(Collectors.joining(" AND "));
    }

    private void registerStatements()
    {
        // FIXME this is registered multiple times
        try
        {
            database.get().createPreparedStatement(PreparedStatements.STATEMENT_SELECT_ROW, statementFormat);
            alive.set(true);
        }
        catch (final Throwable t)
        {
            t.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> request(final ServerStorageKey<T> key)
    {
        checkOpen();

        if (key.get().size() != mapper.getPlan().getNumberOfKeys())
            throw new BadKeyException(key.get().size(), mapper.getPlan().getNumberOfKeys());

        final ServerStorageStructure result = cache.getIfPresent(key.hashCode());
        if (result != null)
            return Optional.of((T) result);

        return Optional.ofNullable(newStructureFromResult(createResultSetFromKey(key)));
    }

    @Override
    public List<T> requestWhere(final String where, final Object... args)
    {
        for (int i = 0; i < args.length; ++i)
            if (args[i] instanceof String)
                args[i] = ("\"" + args[i].toString() + "\"");

        return requestWhere(String.format(where, args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> requestWhere(final String where)
    {
        checkOpen();

        final List<T> list = new ArrayList<>();

        try (ResultSet result = database.get().execute(selectLowPart + where))
        {
            while (result.next())
                list.add(newStructureFromResult(result));

        }
        catch (final Throwable t)
        {
            throw new DatabaseConnectionException(t);
        }

        return list;
    }

    private ResultSet createResultSetFromKey(final ServerStorageKey<T> key)
    {
        final ResultSet result;
        try
        {
            result = database.get().preparedExecute(PreparedStatements.STATEMENT_SELECT_ROW, key.get().toArray());
        }
        catch (final Throwable e)
        {
            throw new WrongDatabaseStructureException(structureName, e);
        }

        return result;
    }

    private T newStructureFromResult(final ResultSet result)
    {
        try
        {
            if (result.isBeforeFirst())
                result.first();
        }
        catch (final Exception e)
        {
            throw new WrongDatabaseStructureException(structureName, e);
        }

        /*TODO @FrameworkIntegration:Trace
        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Mapping result\"%s\" to new \"%s\"", preparedStatement, structureName));
         */

        return initStructure(mapper.map(result), false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(final ServerStorageKey<T> key)
    {
        final ServerStorageStructure structure = mapper.createEmpty(key.get());

        final T record;
        try
        {
            record = (T)structure;
        }
        catch (final Throwable t)
        {
            throw new RuntimeException(String.format("Could not cast class %s to your parameter! Check config!", structure.getClass()), t);
        }
        return initStructure(record, true);
    }

    /**
     * @param structure
     * @return The record in cache if exists or the record itself and cache it.
     */
    private T initStructure(final T structure, final boolean created)
    {
        final T inCache = cache.getIfPresent(structure.hashCode());
        if (inCache != null)
            return inCache;

        cache.put(structure.hashCode(), structure);

        final ServerStorageStructurePrivateBase privateBase = ((ServerStorageStructurePrivateBase)structure);

        privateBase.setOwnerAndTracker(this, changeTracker);

        if (created)
            privateBase.onCreate();

        return structure;
    }

    protected boolean setValueOfObservable(final ReadOnlyProperty<?> property, final Object value)
    {
        return mapper.set(MappingBeans.getMetaData(property).getName(), property, value);
    }

    protected boolean resetValueOfObservable(final ReadOnlyProperty<?> property)
    {
        return mapper.reset(MappingBeans.getMetaData(property).getName(), property);
    }

    private Pair<TableSchema, Function<MappingMetaData, Optional<TypeToken<?>>>> estimateSchema(final TableSchema providedSchema)
    {
        final Map<String, MappingMetaData> providedData = providedSchema.getEntries()
                .stream()
                .distinct()
                .collect(Collectors.toMap(MappingMetaData::getName, metaData -> metaData));

        final List<MappingMetaData> metaDataList = new ArrayList<>(providedSchema.getEntries().size());
        final Map<MappingMetaData, TypeToken<?>> types = new HashMap<>();

        try (final ResultSet result = database.get().execute("SHOW COLUMNS FROM " + tableName))
        {
            while (result.next())
            {
                final String name = result.getString("Field");
                final boolean isPrimaryKey = result.getString("Key").equals("PRI");

                final String defaultValue;
                {
                    final String cache = result.getString("Default");
                    if (cache == null || "null".equals(cache))
                        defaultValue = "";
                    else
                        defaultValue = cache;
                }

                final TypeToken<?> type = getJavaTypeOf(result.getString("Type"), isPrimaryKey);

                final MappingMetaData finalMetaData;

                final MappingMetaData data = providedData.get(name);
                if (Objects.nonNull(data))
                {
                    // Complete info
                    finalMetaData = new AbstractMappingMetaData()
                    {
                        @Override
                        public String getName()
                        {
                            return data.getName();
                        }

                        @Override
                        public String getTarget()
                        {
                            return data.getTarget();
                        }

                        @Override
                        public String getDescription()
                        {
                            return data.getDescription();
                        }

                        @Override
                        public String getDefaultValue()
                        {
                            return data.getDefaultValue().isEmpty() ? defaultValue : data.getDefaultValue();
                        };

                        @Override
                        public int getIndex()
                        {
                            return data.getIndex();
                        }

                        @Override
                        public boolean isKey()
                        {
                            return data.isKey() ? true : isPrimaryKey;
                        }

                        @Override
                        public String getAlias()
                        {
                            return data.getAlias();
                        }
                    };
                }
                else if (providedSchema.getPolicy().hasPermissionToAddColumns())
                {
                    // Add column
                    finalMetaData = new AbstractMappingMetaData()
                    {
                        @Override
                        public String getName()
                        {
                            return name;
                        }

                        @Override
                        public String getDescription()
                        {
                            return "Auto completed";
                        }

                        @Override
                        public String getDefaultValue()
                        {
                            return defaultValue;
                        };

                        @Override
                        public boolean isKey()
                        {
                            return isPrimaryKey;
                        }
                    };
                }
                else
                    continue;

                metaDataList.add(finalMetaData);
                types.put(finalMetaData, type);
            }
        }
        catch (final Throwable t)
        {
            throw new DatabaseConnectionException(t);
        }

        // Get Fields of the table
        return new Pair<>(new TableSchema()
        {
            private final List<MappingMetaData> entries = Collections.unmodifiableList(metaDataList);

            @Override
            public SchemaPolicy getPolicy()
            {
                return providedSchema.getPolicy();
            }

            @Override
            public String getStructure()
            {
                return providedSchema.getStructure();
            }

            @Override
            public String getName()
            {
                return providedSchema.getName();
            }

            @Override
            public ClientStorageFormat getFormat()
            {
                return providedSchema.getFormat();
            }

            @Override
            public List<MappingMetaData> getEntries()
            {
                return entries;
            }
        },
        m -> Optional.ofNullable(types.get(m)));
    }

    private static final TypeToken<?> TOKEN_OF_BOOL = TypeToken.of(BooleanProperty.class);
    private static final TypeToken<?> TOKEN_OF_INT = TypeToken.of(IntegerProperty.class);
    private static final TypeToken<?> TOKEN_OF_INT_KEY = TypeToken.of(ReadOnlyIntegerProperty.class);
    private static final TypeToken<?> TOKEN_OF_LONG = TypeToken.of(LongProperty.class);
    private static final TypeToken<?> TOKEN_OF_FLOAT = TypeToken.of(FloatProperty.class);
    private static final TypeToken<?> TOKEN_OF_DOUBLE = TypeToken.of(DoubleProperty.class);
    private static final TypeToken<?> TOKEN_OF_STRING = TypeToken.of(StringProperty.class);
    private static final TypeToken<?> TOKEN_OF_STRING_KEY = TypeToken.of(ReadOnlyStringProperty.class);

    private TypeToken<?> getJavaTypeOf(final String string, final boolean isPrimaryKey)
    {
        // FIXME Find a better way for this since ResultSet.getObject() is no option
        if (string.contains("bit"))
            return TOKEN_OF_BOOL;
        else if (string.contains("bool") || string.contains("tinyint")
              || string.contains("smallint") || string.contains("mediumint"))
            return isPrimaryKey ? TOKEN_OF_INT_KEY : TOKEN_OF_INT;
        else if (string.contains("int"))
            return TOKEN_OF_LONG;
        else if (string.contains("float"))
            return TOKEN_OF_FLOAT;
        else if (string.contains("double") || string.contains("decimal"))
            return TOKEN_OF_DOUBLE;
        else
            return isPrimaryKey ? TOKEN_OF_STRING_KEY : TOKEN_OF_STRING;
    }

    @Override
    public String toString()
    {
        final Map<Integer, T> map = cache.asMap();
        return Arrays.toString(map.entrySet().toArray()).replace("],", "],\n");
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((databaseId == null) ? 0 : databaseId.hashCode());
        result = prime * result
                + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        final ServerStorageImpl other = (ServerStorageImpl) obj;
        if (databaseId != other.databaseId)
            return false;
        if (tableName == null)
        {
            if (other.tableName != null)
                return false;
        }
        else if (!tableName.equals(other.tableName))
            return false;
        return true;
    }
}
