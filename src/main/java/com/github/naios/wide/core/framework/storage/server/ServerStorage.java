package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.StorageException;
import com.github.naios.wide.core.framework.storage.StorageName;
import com.github.naios.wide.core.framework.util.ClassUtil;
import com.github.naios.wide.core.session.database.DatabaseType;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

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
    public DatabaseConnectionException()
    {
        super("Something went wrong with the database!");
    }
}

@SuppressWarnings("serial")
class NoDefinedTableNameException extends ServerStorageException
{
    public NoDefinedTableNameException(final Class<?> type)
    {
        super(String.format("Structure %s defines no @StorageName!", type.getName()));
    }
}

public class ServerStorage<T extends ServerStorageStructure>
{
    private final Class<? extends ServerStorageStructure> type;

    private final List<Field> keys = new LinkedList<>();

    private final Map<Integer, ServerStorageStructure> cache = new HashMap<>();

    private final DatabaseType database;

    private final String statementFormat;

    private final String tableName;

    private PreparedStatement statement;

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database) throws ServerStorageException
    {
        this (type, database, GetTableName(type, type));
    }

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database, final String tableName) throws ServerStorageException
    {
        this.type = type;
        this.database = database;
        this.tableName = tableName;

        // Store keys into this.keys
        for (final Field field : getAllAnnotatedFields())
        {
            final ServerStorageType fieldType = ServerStorageType.SelectTypeOfField(field);
            if (fieldType == null)
                throw new IllegalTypeException(field.getType());

            if (field.getAnnotation(ServerStorageEntry.class).key())
                if (fieldType.getIsPossibleKey())
                    keys.add(field);
                else
                    throw new IllegalTypeAsKeyException(field.getType());
        }

        if (keys.isEmpty())
            throw new NoKeyException(type);

        statementFormat = createStatementFormat();

        initStatements();
    }

    // Looks recursively for StorageName annotation
    private static String GetTableName(final Class<? extends ServerStorageStructure> base,
            final Class<?> type) throws StorageException
    {
        if (type == null)
            throw new NoDefinedTableNameException(base);

        final StorageName name = type.getAnnotation(StorageName.class);
        if (name != null)
            return name.name();

        return GetTableName(base, type.getSuperclass());
    }

    public String getTableName()
    {
        return tableName;
    }

    private String getNameofField(final Field field)
    {
        final ServerStorageEntry annotation = field.getAnnotation(ServerStorageEntry.class);

        if (!annotation.name().equals(""))
            return annotation.name();
        else
            return field.getName();
    }

    private String createStatementFormat()
    {
        final StringBuilder builder = new StringBuilder("SELECT ");
        for (final Field field : getAllAnnotatedFields())
        {
            builder
                .append(getNameofField(field))
                .append(", ");
        }

        builder.setLength(builder.length() - 2);
        builder.trimToSize();

        builder
            .append(" FROM ")
            // TODO
            .append(tableName)
            .append(" WHERE ");

        for (final Field field : keys)
        {
            builder
                .append(getNameofField(field))
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
                createStatement();
            }
        });

        WIde.getHooks().addListener(new HookListener(Hook.ON_DATABASE_CLOSE, this)
        {
            @Override
            public void informed()
            {
                deleteStatement();
            }
        });

        if (WIde.getDatabase().isConnected())
            createStatement();
    }

    private void createStatement()
    {
        try
        {
            statement = WIde.getDatabase().getConnection(database).prepareStatement(statementFormat);
        } catch (final SQLException e)
        {
            throw new DatabaseConnectionException();
        }
    }

    private void deleteStatement()
    {
        try
        {
            statement.close();
        } catch (final SQLException e)
        {
        }

        statement = null;
    }

    @SuppressWarnings("unchecked")
    public T get(final Object... keysOfEntry)
    {
        if (keysOfEntry.length != keys.size())
            throw new BadKeyException(keysOfEntry.length, keys.size());

        // If the Object is already cached return it
        final int hash = calculateHashOfKeys(keysOfEntry);
        if (cache.containsKey(hash))
            return (T) cache.get(hash);

        final ServerStorageStructure record;
        try
        {
            record = type.newInstance();

        } catch (final Exception e)
        {
            throw new BadMappingException(type);
        }

        record.setOwner(this);
        cache.put(hash, record);

        mapStructureWithKey(record, keysOfEntry);

        return (T) record;
    }

    private void mapStructureWithKey(final ServerStorageStructure record, final Object[] keys)
    {
        if (statement == null)
            throw new DatabaseConnectionException();

        for (int i = 0; i < keys.length; ++i)
                try
                {
                    if (keys[i] instanceof String)
                        statement.setString(i+1, (String)keys[i]);
                    else if (keys[i] instanceof Integer)
                        statement.setInt(i+1, (int)keys[i]);

                } catch (final SQLException e)
                {
                    throw new DatabaseConnectionException();
                }

        final ResultSet result;
        try
        {
            result = statement.executeQuery();
            result.first();

            for (final Field field : getAllAnnotatedFields())
            {
                if (!field.isAccessible())
                    field.setAccessible(true);

                final ServerStorageType fieldType = ServerStorageType.SelectTypeOfField(field);
                final Object obj = fieldType.createFromResult(result, getNameofField(field));

                field.set(record, obj);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            throw new DatabaseConnectionException();
        }
    }

    private int calculateHashOfKeys(final Object[] keys)
    {
        return Arrays.hashCode(keys);
    }

    private Field[] getAllAnnotatedFields()
    {
        return ClassUtil.getAnnotatedDeclaredFields(type,
                ServerStorageEntry.class, true);
    }
}
