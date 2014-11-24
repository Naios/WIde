package wide.core.framework.storage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private final static int HEADER_SIZE = 20;

    private final static String MAGIC = "WDBC";

    private final Map<Integer, Integer> entryToOffsetCache = new HashMap<>();

    private final Map<Integer, StringInBufferCached> offsetToStringCache = new HashMap<>();

    private final static int COUNT_CHECK_RECORDS_FOR_STRING = 4;

    private final boolean[] isColumnStringTypeCache;

    private class StringInBufferCached
    {
        final int begin, length;

        public StringInBufferCached(ByteBuffer buffer, int begin, int length)
        {
            this.begin = begin;
            this.length = length;
        }

        @Override
        public String toString()
        {
            final byte[] bytes = new byte[length];

            // TODO Find a better way
            // buffer.get(bytes, begin, length); seems to be bugged hard!
            buffer.position(begin);
            for (int i = 0; i < length; ++i)
                bytes[i] = buffer.get();

            try
            {
                return new String(bytes, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }
    }

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

        // String of the String Block offset begin always at StringBlockOffset + 1
        buffer.position(getStringBlockOffset() + 1);

        while (buffer.remaining() > 1)
        {
            // Push buffer forward to the next string
            final int offset = buffer.position();
            while (buffer.get() != 0);

            offsetToStringCache.put(offset, new StringInBufferCached(buffer, offset, buffer.position() - offset - 1));
        }

        // Pre Calculate if a column is a String
        isColumnStringTypeCache = new boolean[fieldsCount];
        for (int x = 0; x < fieldsCount; ++x)
            isColumnStringTypeCache[x] = isFieldPossibleString(x);
    }

    private boolean isFieldPossibleString(int x)
    {
        for (int y = 0; (y < recordsCount) && (y < COUNT_CHECK_RECORDS_FOR_STRING); ++y)
        {
            final int offset = buffer.getInt(getOffset(y, x));
            if (!offsetToStringCache.containsKey(offset + getStringBlockOffset()))
                return false;
        }
        return true;
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
        final StringInBufferCached cached = offsetToStringCache.get(offset);
        if (cached != null)
            return cached.toString();
        else
            return null;
    }

    public String asString(boolean withStrings)
    {
        final int[][] intArray = (!withStrings) ? asIntArray() : null;
        final String[][] stringArray = (withStrings) ? asStringArray() : null;

        final StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        for (int y = 0; y < recordsCount; ++y)
        {
            builder.append("\t{");
            for (int x = 0; x < fieldsCount; ++x)
            {
                if (withStrings)
                {
                    if (isColumnStringTypeCache[x])
                        builder.append("\"");

                    builder.append(stringArray[y][x]);

                    if (isColumnStringTypeCache[x])
                        builder.append("\"");
                }
                else
                    builder.append(intArray[y][x]);

                builder.append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
            builder.append("},\n");
        }

        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString()
    {
        return asString(true);
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

        final T record = create();

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

    public int[][] asIntArray()
    {
        final int[][] array = new int[recordsCount][fieldsCount];
        for (int y = 0; y < recordsCount; ++y)
            for (int x = 0; x < fieldsCount; ++x)
                array[y][x] = buffer.getInt(getOffset(y, x));

        return array;
    }

    public String[][] asStringArray()
    {
        final String[][] array = new String[recordsCount][fieldsCount];
        for (int y = 0; y < recordsCount; ++y)
            for (int x = 0; x < fieldsCount; ++x)
            {
                final int asInt = buffer.getInt(getOffset(y, x));
                final String asString = (isColumnStringTypeCache[x]) ? getStringAtOffset(asInt + getStringBlockOffset()) : null;
                if (asString != null)
                    array[y][x] = asString;
                else
                    array[y][x] = String.valueOf(asInt);
            }

        return array;
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
