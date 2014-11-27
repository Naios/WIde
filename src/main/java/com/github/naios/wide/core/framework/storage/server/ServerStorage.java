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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.github.naios.wide.core.WIde;
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
class DatabaseConnectionException extends ServerStorageException
{
    public DatabaseConnectionException()
    {
        super("Something went wrong with the database!");
    }
}

public class ServerStorage<T extends ServerStorageStructure>
{
    private final Class<? extends ServerStorageStructure> type;

    private final List<Field> keys = new LinkedList<>();

    private final Map<Integer, ServerStorageStructure> cache = new HashMap<>();

    private final DatabaseType database;

    private final String statementFormat;

    private PreparedStatement statement;

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database) throws ServerStorageException
    {
        this.type = type;
        this.database = database;

        // Store keys into this.keys
        for (final Field field : getAllAnnotatedFields())
            if (field.getAnnotation(ServerStorageEntry.class).key())
                keys.add(field);

        if (keys.isEmpty())
            throw new NoKeyException(type);

        statementFormat = createStatementFormat();

        initStatements();
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
            .append("creature_template")
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

                final Object obj;
                // ReadOnlyIntegerProperty
                if (field.getType().equals(ReadOnlyIntegerProperty.class))
                    obj = new ReadOnlyIntegerWrapper(result.getInt(getNameofField(field)));
                // SimpleIntegerProperty
                else if (field.getType().equals(IntegerProperty.class))
                    obj = new SimpleIntegerProperty(result.getInt(getNameofField(field)));
                // ReadOnlyStringProperty
                else if (field.getType().equals(ReadOnlyStringProperty.class))
                    obj = new ReadOnlyStringWrapper(result.getString(getNameofField(field)));
                // SimpleStringProperty
                else if (field.getType().equals(StringProperty.class))
                    obj = new SimpleStringProperty(result.getString(getNameofField(field)));
                else
                    obj = null;

                field.set(record, obj);
            }

        } catch (final Exception e)
        {
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
