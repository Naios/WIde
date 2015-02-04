
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.database.Database;
import com.github.naios.wide.api.framework.storage.server.ChangeTracker;
import com.github.naios.wide.api.framework.storage.server.ServerStorage;
import com.github.naios.wide.api.framework.storage.server.ServerStorageException;
import com.github.naios.wide.api.framework.storage.server.ServerStorageKey;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.util.CrossIterator;
import com.github.naios.wide.api.util.Pair;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.github.naios.wide.framework.internal.storage.mapping.JsonMapper;
import com.github.naios.wide.framework.internal.storage.mapping.Mapper;
import com.github.naios.wide.framework.internal.storage.mapping.MappingAdapterHolder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
    public DatabaseConnectionException(final String msg)
    {
        super(String.format("Something went wrong with the database! (%s)", msg));
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

    private final Cache<Integer /*hash*/, ServerStorageStructure /*entity*/> cache =
            CacheBuilder.newBuilder().weakValues().build();

    private final ObjectProperty<Database> database =
            new SimpleObjectProperty<Database>();

    private final BooleanProperty alive =
            new SimpleBooleanProperty();

    private final String databaseId;

    private final String statementFormat, selectLowPart, tableName;

    private final Mapper<ResultSet, T, ObservableValue<?>> mapper;

    private final String structureName;

    private final ChangeTrackerImpl changeTracker;

    public ServerStorageImpl(final String databaseId, final String tableName, final ChangeTrackerImpl changeTracker) throws ServerStorageException
    {
        this.databaseId = databaseId;
        this.tableName = tableName;
        this.changeTracker = changeTracker;

        final TableSchema schema = FrameworkServiceImpl.getConfigService().getActiveEnviroment()
                .getDatabaseConfig(databaseId).schema().get().getSchemaOf(tableName);

        this.structureName = schema.getStructure();

        @SuppressWarnings("unchecked")
        final MappingAdapterHolder<ResultSet, T, ObservableValue<?>> adapter =
                (MappingAdapterHolder<ResultSet, T, ObservableValue<?>>) SQLToPropertyMappingAdapterHolder.INSTANCE;

        mapper = new JsonMapper<ResultSet, T, ObservableValue<?>>(schema, adapter,
                Arrays.asList(ServerStorageStructurePrivateBase.class), ServerStorageStructureBaseImplementation.class);

        selectLowPart = createSelectFormat();
        statementFormat = createStatementFormat();

        this.database.addListener(new ChangeListener<Database>()
        {
            @Override
            public void changed(
                    final ObservableValue<? extends Database> observable,
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

        this.database.bind(FrameworkServiceImpl.getDatabasePoolService().requestConnection(databaseId));
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
        return StringUtil.fillWithSpaces("SELECT",
                StringUtil.concat(", ", new CrossIterator<>(mapper.getPlan().getMetadata(), metadata -> metadata.getName())),
                    "FROM", tableName, "WHERE ");
    }

    private String createStatementFormat()
    {
        return selectLowPart +
                StringUtil.concat(" ", new CrossIterator<>(mapper.getPlan().getKeys(), metadata -> metadata.getName() + "=?"));
    }

    private void registerStatements()
    {
        // FIXME this is registered multiple times
        database.get().createPreparedStatement(PreparedStatements.STATEMENT_SELECT_ROW, statementFormat);
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

        return Optional.ofNullable((T) newStructureFromResult(createResultSetFromKey(key)));
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

        final List<T> list = new ArrayList<T>();
        final ResultSet result;
        try
        {
            result = database.get().execute(selectLowPart + where);

            while (result.next())
                list.add((T) newStructureFromResult(result));

        } catch (final SQLException e)
        {
            throw new DatabaseConnectionException(e.getMessage());
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

    private ServerStorageStructure newStructureFromResult(final ResultSet result)
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

    @Override
    @SuppressWarnings("unchecked")
    public T create(final ServerStorageKey<T> key)
    {
        final ServerStorageStructure record = mapper.createEmpty(key.get());
        return (T) initStructure(record, true);
    }

    /**
     * @param structure
     * @return The record in cache if exists or the record itself and cache it.
     */
    private ServerStorageStructure initStructure(final ServerStorageStructure structure, final boolean created)
    {
        final ServerStorageStructure inCache = cache.getIfPresent(structure.hashCode());
        if (inCache != null)
            return inCache;

        cache.put(structure.hashCode(), structure);

        final ServerStorageStructurePrivateBase privateBase = ((ServerStorageStructurePrivateBase)structure);

        privateBase.setOwnerAndTracker(this, changeTracker);

        if (created)
            privateBase.onCreate();

        return structure;
    }

    protected boolean setValueOfObservable(final Pair<ObservableValue<?>, MappingMetaData> entry, final Object value)
    {
        return mapper.set(entry.second().getName(), entry.first(), value);
    }

    protected boolean resetValueOfObservable(final Pair<ObservableValue<?>, MappingMetaData> entry)
    {
        return mapper.reset(entry.second().getName(), entry.first());
    }

    @Override
    public String toString()
    {
        final ConcurrentMap<Integer, ServerStorageStructure> map = cache.asMap();
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
