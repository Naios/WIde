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

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.StorageStructure;
import com.github.naios.wide.core.framework.util.ClassUtil;
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

public class ServerStorage<T extends ServerStorageStructure> implements AutoCloseable
{
    private final Class<? extends ServerStorageStructure> type;

    private final List<Field> keys;

    private final Cache<Integer /*hash*/, ServerStorageStructure /*entity*/> cache =
            CacheBuilder.newBuilder().weakValues().build();

    private final DatabaseType database;

    private final String statementFormat, selectLowPart, tableName;

    private PreparedStatement preparedStatement;

    private Statement statement;

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database) throws ServerStorageException
    {
        this (type, database, StorageStructure.getStorageName(type));
    }

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database, final String tableName) throws ServerStorageException
    {
        this.type = type;
        this.database = database;
        this.tableName = tableName;

        // Store keys into this.keys
        keys = ServerStorageStructure.getPrimaryFields(type);

        if (keys.isEmpty())
            throw new NoKeyException(type);

        for (final Field field : keys)
        {
            final ServerStorageType fieldType = ServerStorageType.getType(field);
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

        initStatements();
    }

    public String getTableName()
    {
        return tableName;
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

        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_CLOSED, this)
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
            final Connection con = WIde.getDatabase().getConnection(database);

            statement = con.createStatement();
            preparedStatement = con.prepareStatement(statementFormat);

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
            for (final Field field : record.getAllFields())
                ServerStorageType.doMapFieldToRecordFromResult(field, record, result);
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

        record.setState(ServerStorageStructureState.STATE_NEW);

        final List<Field> primaryFields = record.getPrimaryFields();
        assert primaryFields.size() == key.get().length;

        for (final Field field : record.getAllFields())
        {
            if (primaryFields.contains(field))
            {
                final int idx = primaryFields.indexOf(field);
                ServerStorageType.doMapFieldToRecordFromObject(field, record, key.get()[idx]);
            }
            else
            {
                ServerStorageType.doMapFieldToRecordFromObject(field, record, null);
            }
        }

        return (T) record;
    }

    private Field[] getAllAnnotatedFields()
    {
        return ClassUtil.getAnnotatedDeclaredFields(type,
                ServerStorageEntry.class, true);
    }

    protected void valueChanged(final ServerStorageStructure record, final Field field, final ObservableValue<?> observable, final Object oldValue)
    {
        ServerStorageChangeHolder.instance().insert(new ObservableValueStorageInfo(getTableName(), record, field), observable, oldValue);
    }

    protected void structureDeleted(final ServerStorageStructure serverStorageStructure)
    {
        // TODO
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
}
