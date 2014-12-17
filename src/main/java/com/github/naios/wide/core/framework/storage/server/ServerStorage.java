
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.MappingAdapterHolder;
import com.github.naios.wide.core.framework.storage.mapping.schema.Schema;
import com.github.naios.wide.core.framework.storage.mapping.schema.SchemaCache;
import com.github.naios.wide.core.framework.storage.mapping.templates.SQLToPropertyMappingAdapterHolder;
import com.github.naios.wide.core.framework.storage.server.builder.SQLBuilder;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.storage.server.helper.StructureState;
import com.github.naios.wide.core.framework.util.CrossIterator;
import com.github.naios.wide.core.framework.util.StringUtil;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@SuppressWarnings("serial")
class NoKeyException extends ServerStorageException
{
    public NoKeyException(final Class<? extends ServerStorageStructure> type)
    {
        super(String.format("Structure %s defines no keys!", type));
    }
}

@SuppressWarnings("serial")
class BadKeyException extends ServerStorageException
{
    public BadKeyException(final int givenKeyCount, final int structureKeyCount)
    {
        super(String.format("Count of passed keys {%s} does not match to the count of the structure {%s}!", givenKeyCount, structureKeyCount));
    }
}

@SuppressWarnings("serial")
class BadMappingException extends ServerStorageException
{
    public BadMappingException(final Class<? extends ServerStorageStructure> type)
    {
        super(String.format("Can't create or map to the structure %s", type.getName()));
    }
}

@SuppressWarnings("serial")
class IllegalTypeException extends ServerStorageException
{
    public IllegalTypeException(final Class<?> type)
    {
        super(String.format("Class can't be used in StorageStructures %s", type.getName()));
    }
}

@SuppressWarnings("serial")
class IllegalTypeAsKeyException extends ServerStorageException
{
    public IllegalTypeAsKeyException(final Class<?> type)
    {
        super(String.format("Class %s can't be used as key in StorageStructures", type.getName()));
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
    public WrongDatabaseStructureException(final Class<?> type, final String msg)
    {
        super(String.format("Your database structure dosn't match to %s (%s)!", type.getName(), msg));
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

public class ServerStorage<T extends ServerStorageStructure> implements AutoCloseable
{
    private final Class<? extends ServerStorageStructure> type;

    private final Cache<Integer /*hash*/, ServerStorageStructure /*entity*/> cache =
            CacheBuilder.newBuilder().weakValues().build();

    private final ObjectProperty<Connection> connection =
            new SimpleObjectProperty<Connection>();

    private final String databaseId;

    private final String statementFormat, selectLowPart, tableName;

    private final Mapper<ResultSet, T, ObservableValue<?>> mapper;

    private final ServerStorageChangeHolder changeHolder;

    private PreparedStatement preparedStatement;

    private Statement statement;

    public ServerStorage(final Class<? extends T> type, final String databaseId, final String tableName) throws ServerStorageException
    {
        this.type = type;
        this.databaseId = databaseId;
        this.tableName = tableName;

        final Schema schema = SchemaCache.INSTANCE.get(WIde.getConfig().get().getActiveEnviroment().getDatabaseConfig(databaseId).schema().get());

        @SuppressWarnings("unchecked")
        final MappingAdapterHolder<ResultSet, T, ObservableValue<?>> adapter =
                (MappingAdapterHolder<ResultSet, T, ObservableValue<?>>) SQLToPropertyMappingAdapterHolder.INSTANCE;

        mapper = new JsonMapper<ResultSet, T, ObservableValue<?>>(schema.getSchemaOf(tableName), adapter,
                type, ServerStorageBaseImplementation.class);

        selectLowPart = createSelectFormat();
        statementFormat = createStatementFormat();

        this.connection.addListener(new ChangeListener<Connection>()
        {
            @Override
            public void changed(
                    final ObservableValue<? extends Connection> observable,
                    final Connection oldValue, final Connection newValue)
            {
                initStatements();
            }
        });

        this.connection.bind(WIde.getDatabase().connection(databaseId));

        this.changeHolder = ServerStorageChangeHolderFactory.instance(databaseId);
    }

    public String getTableName()
    {
        return tableName;
    }

    public String getDatabaseId()
    {
        return databaseId;
    }

    public ServerStorageChangeHolder getChangeHolder()
    {
        return changeHolder;
    }

    public boolean isOpen()
    {
        // If the connection gets closed the statements are set to null
        return preparedStatement != null;
    }

    private void checkOpen()
    {
        if (!isOpen())
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

    private void initStatements()
    {
        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_ESTABLISHED, this)
        {
            @Override
            public void informed()
            {
                createStatements();
            }
        });

        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_CLOSE, this)
        {
            @Override
            public void informed()
            {
                deleteStatements();
            }
        });

        if (WIde.getDatabase().isConnected())
            createStatements();
    }

    private void createStatements()
    {
        try
        {
            statement = connection.get().createStatement();
            preparedStatement = connection.get().prepareStatement(statementFormat);

        }
        catch (final SQLException e)
        {
            throw new DatabaseConnectionException(e.getMessage());
        }
    }

    private void deleteStatements()
    {
        try
        {
            if (statement != null)
                statement.close();

            if (preparedStatement != null)
                preparedStatement.close();

        } catch (final SQLException e)
        {
        }
        finally
        {
            statement = null;
            preparedStatement = null;
            cache.invalidateAll();
        }
    }

    /**
     * @param record
     * @return The record in cache if exists or the record itself and cache it.
     */
    private ServerStorageStructure getAndCache(final ServerStorageStructure record)
    {
        final int hash = record.hashCode();
        final ServerStorageStructure inCache = cache.getIfPresent(hash);
        if (inCache != null)
            return inCache;

        cache.put(hash, record);
        return record;
    }

    @SuppressWarnings("unchecked")
    public T get(final ServerStorageKey<T> key)
    {
        checkOpen();

        if (key.get().size() != mapper.getPlan().getNumberOfKeys())
            throw new BadKeyException(key.get().size(), mapper.getPlan().getNumberOfKeys());

        return (T) getAndCache(newStructureFromResult(createResultSetFromKey(key)));
    }

    public List<T> getWhere(final String where, final Object... args)
    {
        for (int i = 0; i < args.length; ++i)
            if (args[i] instanceof String)
                args[i] = ("\"" + args[i].toString() + "\"");

        return getWhere(String.format(where, args));
    }

    @SuppressWarnings("unchecked")
    public List<T> getWhere(final String where)
    {
        checkOpen();

        final List<T> list = new ArrayList<T>();
        final ResultSet result;
        try
        {
            result = statement.executeQuery(selectLowPart + where);

            while (result.next())
                list.add((T) getAndCache(newStructureFromResult(result)));

        } catch (final SQLException e)
        {
            throw new DatabaseConnectionException(e.getMessage());
        }

        return list;
    }

    private ResultSet createResultSetFromKey(final ServerStorageKey<T> key)
    {
        if (preparedStatement == null)
            throw new DatabaseConnectionException("Statement is null");

        for (int i = 0; i < key.get().size(); ++i)
                try
                {
                    preparedStatement.setString(i + 1, key.get(i).toString());

                } catch (final SQLException e)
                {
                    throw new DatabaseConnectionException(e.getMessage());
                }

        final ResultSet result;
        try
        {
            result = preparedStatement.executeQuery();
        }
        catch (final Exception e)
        {
            throw new WrongDatabaseStructureException(type, e.getMessage());
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
            throw new WrongDatabaseStructureException(type, e.getMessage());
        }

        if (WIde.getEnviroment().isTraceEnabled())
            System.out.println(String.format("Mapping result\"%s\" to new \"%s\"", preparedStatement, type.getName()));

        final ServerStorageStructure record = mapper.map(result);
        onStructureCreated(record);
        return record;
    }

    @SuppressWarnings("unchecked")
    public T create(final ServerStorageKey<T> key)
    {
        final ServerStorageStructure createdRecord = mapper.createEmpty(key.get()), record;

        record = getAndCache(createdRecord);
        if (record == createdRecord)
            onStructureCreated(record);

        return (T) record;
    }

    private void checkInvalidAccess(final ServerStorageStructure storage)
    {
        if (!((ServerStorageBaseImplementation)storage).writeableState().get().isAlive())
            throw new AccessedDeletedStructureException(storage);
    }

    protected void onValueChanged(final ServerStorageStructure storage, final String name,
            final ObservableValue<?> observable, final Object oldValue)
    {
        checkInvalidAccess(storage);

        ((ServerStorageBaseImplementation)storage).writeableState().set(StructureState.STATE_UPDATED);
        changeHolder.insert(new ObservableValueStorageInfo(storage, name), observable, oldValue);
    }

    protected void onStructureCreated(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);

        ((ServerStorageBaseImplementation)storage).writeableState().set(StructureState.STATE_CREATED);
        changeHolder.create(storage);
    }

    protected void onStructureDeleted(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);
        changeHolder.delete(storage);

        storage.reset();

        ((ServerStorageBaseImplementation)storage).writeableState().set(StructureState.STATE_DELETED);
    }


    protected void onStructureReset(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);
        changeHolder.reset(storage);
    }

    protected boolean setValueOfObservable(final String name, final ObservableValue<?> observable, final Object value)
    {
        return mapper.set(name, observable, value);
    }

    protected boolean resetValueOfObservable(final String name,
            final ObservableValue<?> observable)
    {
        return mapper.reset(name, observable);
    }

    public SQLBuilder createBuilder()
    {
        // TODO
        return new SQLBuilder(getChangeHolder(), true);
    }

    @Override
    public void close()
    {
        deleteStatements();
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
        final ServerStorage other = (ServerStorage) obj;
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
