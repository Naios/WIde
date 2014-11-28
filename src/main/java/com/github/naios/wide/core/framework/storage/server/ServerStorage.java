package com.github.naios.wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    public DatabaseConnectionException(final String msg)
    {
        super(String.format("Something went wrong with the database! (%s)", msg));
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

@SuppressWarnings("serial")
class WrongDatabaseStructureException extends ServerStorageException
{
    public WrongDatabaseStructureException(final Class<?> type, final String msg)
    {
        super(String.format("Your database structure dosn't match to %s (%s)!", type.getName(), msg));
    }
}

public class ServerStorage<T extends ServerStorageStructure>
{
    private final Class<? extends ServerStorageStructure> type;

    private final List<Field> keys = new LinkedList<>();

    private final Map<Integer, ServerStorageStructure> cache = new HashMap<>();

    private final DatabaseType database;

    private final String statementFormat, selectLowPart;

    private final String tableName;

    private PreparedStatement preparedStatement;

    private Statement statement;

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

        selectLowPart = createSelectFormat();
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

    private String createSelectFormat()
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
            preparedStatement.close();
            statement.close();
        } catch (final SQLException e)
        {
        }

        statement = null;
        preparedStatement = null;
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



        final ServerStorageStructure record = newStructureFromResult(createResultSetFromKeys(keysOfEntry));

        cache.put(hash, record);

        return (T) record;
    }

    public List<T> getFromWhereQuery(final String where, final Object... args)
    {
        return getFromWhereQuery(String.format(where, args));
    }

    @SuppressWarnings("unchecked")
    public List<T> getFromWhereQuery(final String where)
    {
        final List<T> list = new ArrayList<T>();
        final ResultSet result;
        try
        {
            result = statement.executeQuery(selectLowPart + where);

            while (result.next())
                list.add((T) newStructureFromResult(result));

        } catch (final SQLException e)
        {
            throw new DatabaseConnectionException(e.getMessage());
        }

        return list;
    }

    private ResultSet createResultSetFromKeys(final Object[] keys)
    {
        if (preparedStatement == null)
            throw new DatabaseConnectionException("Statement is null");

        for (int i = 0; i < keys.length; ++i)
                try
                {
                    preparedStatement.setString(i + 1, keys[i].toString());

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

        } catch (final Exception e)
        {
            throw new WrongDatabaseStructureException(type, e.getMessage());
        }

        final ServerStorageStructure record;
        try
        {
            record = type.newInstance();

        } catch (final Exception e)
        {
            throw new BadMappingException(type);
        }

        record.setOwner(this);

        try
        {
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
            throw new WrongDatabaseStructureException(type, e.getMessage());
        }

        return record;
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
