package wide.core.framework.storage.server;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wide.core.framework.util.ClassUtil;

public class ServerStorage<T>
{
    private final Class<? extends ServerStorageStructure> type;

    private final Set<Field> keys = new HashSet<>();

    private final Map<Long, T> cache = new HashMap<>();

    public ServerStorage(final Class<? extends ServerStorageStructure> type) throws ServerStorageException
    {
        this.type = type;

        // Store keys into this.keys
        for (final Field field : getAllAnnotatedFields())
            if (field.getAnnotation(ServerStorageEntry.class).key())
                keys.add(field);
    }

    public T get(final Object... keys)
    {
        // If the Object is already cached return it
        final int hash = calculateHashOfKeys(keys);
        if (cache.containsKey(hash))
            return cache.get(hash);

        return null;
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
