package wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wide.core.WIde;
import wide.core.framework.util.ClassUtil;
import wide.core.session.database.DatabaseType;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;

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
        super("Something went wronf with the database!");
    }
}

public class ServerStorage<T extends ServerStorageStructure>
{
    private final Class<? extends ServerStorageStructure> type;

    private final List<Field> keys = new LinkedList<>();

    private final Map<Integer, ServerStorageStructure> cache = new HashMap<>();

    private final DatabaseType database;

    private PreparedStatement stmt;

    public ServerStorage(final Class<? extends ServerStorageStructure> type, final DatabaseType database) throws ServerStorageException
    {
        this.type = type;
        this.database = database;

        // Store keys into this.keys
        for (final Field field : getAllAnnotatedFields())
            if (field.getAnnotation(ServerStorageEntry.class).key())
                keys.add(field);

        initStatements();
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
            stmt = WIde.getDatabase().getConnection(database).prepareStatement("");
        } catch (final SQLException e)
        {
            throw new DatabaseConnectionException();
        }
    }

    private void deleteStatement()
    {
        try
        {
            stmt.close();
        } catch (final SQLException e)
        {
        }

        stmt = null;
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

        return (T) record;
    }

    private void mapStructureWithKey(final ServerStorageStructure record, final Object[] keys)
    {

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
