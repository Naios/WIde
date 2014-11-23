package wide.core.framework.storage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings("serial")
class OutOfBoundsException extends Exception
{
    public OutOfBoundsException()
    {
        super("Range is out of Bounds!");
    }
}

@SuppressWarnings("serial")
class KeyIsNoIntException extends Exception
{
    public KeyIsNoIntException()
    {
        super("Given DBC Structure has a Key assigned that isn't an int, impossible!");
    }
}

@SuppressWarnings("serial")
class MissingKeyException extends Exception
{
    public MissingKeyException()
    {
        super("Given DBC Structure has no Key assigned!");
    }
}

/**
 * Implementation of Blizzards DBC files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DBC
 */
public abstract class DBCStorage<T> extends Storage<T> implements
        StorageFactory<T>
{
    private final static int            HEADER_SIZE      = 20;
    private final static String         MAGIC            = "WDBC";

    private final Map<Integer, Integer> entryToOffsetCache = new HashMap<>();

    public DBCStorage(String path) throws Exception
    {
        super(path);

        // Pre process data
        // Map indexes to row offsets
        List<Integer> keys = new LinkedList<>();
        // Get indexes of fields marked as key
        for (final Field field : getAllAnnotatedFields())
        {
            final StorageEntry annotation = field.getAnnotation(StorageEntry.class);
            if (annotation.key())
                if (field.getType().equals(int.class))
                    keys.add(annotation.idx());
                else
                    throw new KeyIsNoIntException();
        }

        // Structure needs to define at least 1 key
        if (keys.isEmpty())
            throw new MissingKeyException();

        // Do we need support for multiple keys?
        // Currently we don't support it, but maybe we need it later
        if (keys.size() > 1)
            throw new UnsupportedOperationException();

        for (int i = 0; i < recordsCount; ++i)
        {
            // Gets the entry
            final int entry = buffer.get(getOffset(i, keys.get(0)));

            // Store it with its offset
            entryToOffsetCache.put(entry, getOffset(i, 0));
        }
    }

    private Field[] getAllAnnotatedFields()
    {
        final T record = create();
        return ClassUtil.getAnnotatedDeclaredFields(record.getClass(),
                StorageEntry.class, true);
    }

    private int getOffset(int y, int x)
    {
        if ((y < 0 || y >= recordsCount) || (x < 0 || x >= fieldsCount))
        {
            // Should not occur
            assert false;
            return 0;
        }

        return getHeaderSize() + (y * recordSize) + (x * getFieldSize());
    }

    @Override
    protected int getHeaderSize()
    {
        return HEADER_SIZE;
    }

    @Override
    protected String getMagicSig()
    {
        return MAGIC;
    }

    /**
     * Returns the in memory null terminated string at the offset
     *
     * @param offset
     * @return The string at offset
     */
    private String getStringAtOffset(int offset)
    {
        final List<Byte> list = new Vector<>();
        buffer.position(offset);

        byte b = ' ';
        while ((b = buffer.get()) != 0)
            list.add(b);

        // TODO improve this, found no better way
        final byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i)
            bytes[i] = list.get(i);

        try
        {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        for (int y = 0; y < recordsCount; ++y)
        {
            builder.append("\t{");
            for (int x = 0; x < fieldsCount; ++x)
                builder.append(buffer.getInt(getOffset(y, x))).append(", ");

            builder.delete(builder.length() - 2, builder.length());
            builder.append("},\n");
        }

        builder.append("}");
        return builder.toString();
    }

    public T getEntry(int entry)
    {
        final int offset = entryToOffsetCache.get(entry);
        if (offset == 0)
            return null;
        else
            return getEntryByOffset(offset);
    }

    private T getEntryByOffset(int offset)
    {
        if (offset >= getStringBlockOffset())
            return null;

        @SuppressWarnings("unchecked")
        T record = create();

        for (final Field field : getAllAnnotatedFields())
        {
            final StorageEntry annotation = field.getAnnotation(StorageEntry.class);

            final int absolut_index = offset + (annotation.idx() * getFieldSize());

            field.setAccessible(true);

            try
            {
                if (field.getType().equals(int.class))
                    field.setInt(record, buffer.getInt(absolut_index));
                else if (field.getType().equals(float.class))
                    field.setFloat(record, buffer.getFloat(absolut_index));
                else if (field.getType().equals(String.class))
                    field.set(record, getStringAtOffset(buffer.getInt(absolut_index) + getStringBlockOffset()));

            }
            catch (Exception e)
            {
                return null;
            }
            finally
            {
                field.setAccessible(false);
            }
        }
        return record;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private int idx = 0;

            @Override
            public boolean hasNext()
            {
                return idx < getRecordsCount();
            }

            @Override
            public T next()
            {
                return getEntryByOffset(getOffset(idx++, 0));
            }
        };
    }
}
