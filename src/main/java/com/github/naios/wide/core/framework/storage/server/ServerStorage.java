
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
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

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.StorageStructure;
import com.github.naios.wide.core.framework.storage.server.builder.SQLBuilder;
import com.github.naios.wide.core.framework.storage.server.helper.ObservableValueStorageInfo;
import com.github.naios.wide.core.framework.storage.server.helper.StructureState;
import com.github.naios.wide.core.session.database.DatabaseType;
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

    private final List<Field> keys;

    private final Cache<Integer /*hash*/, ServerStorageStructure /*entity*/> cache =
            CacheBuilder.newBuilder().weakValues().build();

    private final ObjectProperty<Connection> connection
        = new SimpleObjectProperty<Connection>();

    private final DatabaseType databaseType;

    private final String statementFormat, selectLowPart, tableName;

    private final ServerStorageChangeHolder changeHolder;

    private PreparedStatement preparedStatement;

    private Statement statement;

    public ServerStorage(final Class<? extends ServerStorageStructure> type) throws ServerStorageException
    {
        this (type, ServerStorageStructure.getTableTypeFromStructure(type), ServerStorageStructure.getTableNameFromStructure(type));
    }

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType databaseType) throws ServerStorageException
    {
        this (type, databaseType, ServerStorageStructure.getTableNameFromStructure(type));
    }

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType databaseType, final String tableName) throws ServerStorageException
    {
        this.type = type;
        this.databaseType = databaseType;
        this.tableName = tableName;

        // Store keys into this.keys
        keys = ServerStorageStructure.getPrimaryFields(type);

        if (keys.isEmpty())
            throw new NoKeyException(type);

        for (final Field field : keys)
        {
            final ServerStorageFieldType fieldType = ServerStorageFieldType.getType(field);
            if (fieldType == null)
                throw new IllegalTypeException(field.getType());

            if (!fieldType.isPossibleKey())
                throw new IllegalTypeAsKeyException(field.getType());
        }

        for (final Field field : getAllAnnotatedFields())
            if (!ObservableValue.class.isAssignableFrom(field.getType()))
                throw new WrongDatabaseStructureException
                    (type,  String.format("Field %s isn't an ObservableValue!", field.getName()));

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

        this.connection.bind(WIde.getDatabase().connection(databaseType));

        this.changeHolder = ServerStorageChangeHolderFactory.instance(databaseType);
    }

    public String getTableName()
    {
        return tableName;
    }

    public DatabaseType getDatabaseType()
    {
        return databaseType;
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
        final StringBuilder builder = new StringBuilder("SELECT ");
        for (final Field field : getAllAnnotatedFields())
        {
            builder
                .append(ServerStorageStructure.getNameOfField(field))
                .append(", ");
        }

        builder.setLength(builder.length() - 2);
        builder.trimToSize();

        builder
            .append(" FROM ")
            .append(tableName)
            .append(" WHERE ");

        return builder.toString();
    }

    private String createStatementFormat()
    {
        final StringBuilder builder = new StringBuilder(selectLowPart);
        for (final Field field : keys)
        {
            builder
                .append(ServerStorageStructure.getNameOfField(field))
                .append("=?, ");
        }

        builder.setLength(builder.length() - 2);
        builder.trimToSize();

        return builder.toString();
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

        if (key.get().length != keys.size())
            throw new BadKeyException(key.get().length, keys.size());

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

        for (int i = 0; i < key.get().length; ++i)
                try
                {
                    preparedStatement.setString(i + 1, key.get()[i].toString());

                } catch (final SQLException e)
                {
                    throw new DatabaseConnectionException(e.getMessage());
                }

        final ResultSet result;
        try
        {
            if (WIde.getEnviroment().isTraceEnabled())
                System.out.println(String.format("Mapping result\"%s\" to new \"%s\"", preparedStatement, type.getName()));

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

        final ServerStorageStructure record;
        try
        {
            record = type.getConstructor(getClass()).newInstance(this);
        }
        catch (final Exception e)
        {
            throw new BadMappingException(type);
        }

        try
        {
            for (final Field field : record.getAllFieldsFromThis())
                ServerStorageFieldType.doMapFieldToRecordFromResult(field, record, result);
        }
        catch (final Exception e)
        {
            throw new WrongDatabaseStructureException(type, e.getMessage());
        }

        return record;
    }

    @SuppressWarnings("unchecked")
    public T newStructureFromKey(final ServerStorageKey<T> key)
    {
        final ServerStorageStructure record;
        try
        {
            record = type.getConstructor(getClass()).newInstance(this);
        }
        catch (final Exception e)
        {
            throw new BadMappingException(type);
        }

        record.writeableState().set(StructureState.STATE_CREATED);

        final List<Field> primaryFields = record.getPrimaryFields();
        assert primaryFields.size() == key.get().length;

        for (final Field field : record.getAllFieldsFromThis())
        {
            if (primaryFields.contains(field))
            {
                final int idx = primaryFields.indexOf(field);
                ServerStorageFieldType.doMapFieldToRecordFromObject(field, record, key.get()[idx]);
            }
            else
            {
                ServerStorageFieldType.doMapFieldToRecordFromObject(field, record, null);
            }
        }

        onStructureCreated(record);
        return (T) record;
    }

    private Field[] getAllAnnotatedFields()
    {
        return StorageStructure.getAllFields(type, ServerStorageEntry.class);
    }

    private void checkInvalidAccess(final ServerStorageStructure storage)
    {
        if (!storage.writeableState().get().isAlive())
            throw new AccessedDeletedStructureException(storage);
    }

    protected void onValueChanged(final ServerStorageStructure storage, final Field field, final ObservableValue<?> observable, final Object oldValue)
    {
        checkInvalidAccess(storage);

        storage.writeableState().set(StructureState.STATE_UPDATED);
        changeHolder.insert(new ObservableValueStorageInfo(storage, field), observable, oldValue);
    }

    protected void onStructureCreated(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);

        storage.writeableState().set(StructureState.STATE_CREATED);
        changeHolder.create(storage);
    }

    protected void onStructureDeleted(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);
        changeHolder.delete(storage);

        storage.reset();

        storage.writeableState().set(StructureState.STATE_DELETED);
    }


    protected void onStructureReset(final ServerStorageStructure storage)
    {
        checkInvalidAccess(storage);
        changeHolder.reset(storage);
    }

    public SQLBuilder createBuilder()
    {
        return new SQLBuilder(getChangeHolder(), WIde.getConfig().getProperty(Constants.PROPERTY_SQL_VARIABLES).equals("true"));
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
                + ((databaseType == null) ? 0 : databaseType.hashCode());
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
        if (databaseType != other.databaseType)
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
