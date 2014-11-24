package wide.core.framework.storage.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wide.core.framework.util.ClassUtil;

@SuppressWarnings("serial")
class StorageException extends RuntimeException
{
    public StorageException(String reason)
    {
        super(reason);
    }
}

@SuppressWarnings("serial")
class InvalidDataException extends StorageException
{
    public InvalidDataException(String path, String magic)
    {
        super(String.format("File '%s' isn't valid %s file!", path, magic));
    }
}

@SuppressWarnings("serial")
class CorruptedFileException extends StorageException
{
    public CorruptedFileException(String path)
    {
        super(String.format("Storage File '%s' seems to be corrupted!", path));
    }
}

@SuppressWarnings("serial")
class MissingFileException extends StorageException
{
    public MissingFileException(String path)
    {
        super(String.format("File '%s' isn't existing!", path));
    }
}


@SuppressWarnings("serial")
class OutOfBoundsException extends StorageException
{
    public OutOfBoundsException()
    {
        super("Range is out of Bounds!");
    }
}

@SuppressWarnings("serial")
class KeyIsNoIntException extends StorageException
{
    public KeyIsNoIntException()
    {
        super("Given DBC Structure has a Key assigned that isn't an int, impossible!");
    }
}

@SuppressWarnings("serial")
class MissingKeyException extends StorageException
{
    public MissingKeyException()
    {
        super("Given DBC Structure has no Key assigned!");
    }
}

public abstract class ClientStorage<T> implements Iterable<T>
{
    protected final String path;

    /**
     * The count of records (<b>Y / Rows</b>) of the Storage
     */
    protected final int recordsCount;

    /**
     * The count of fields (<b>X / Columns</b>) of the Storage
     */
    protected final int fieldsCount /*X*/;

    /**
     * The overall size of the record (size of all Fields together).<p>
     * Use {@link #getFieldSize()} the size of a single field.
     */
    protected final int recordSize;

    /**
     * The size of the String Block.
     */
    protected final int stringBlockSize;

    protected final ByteBuffer buffer;

    protected final Map<Integer, Integer> entryToOffsetCache = new HashMap<>();

    protected final Map<Integer, StringInBufferCached> offsetToStringCache = new HashMap<>();

    protected final static int COUNT_CHECK_RECORDS_FOR_STRING = 4;

    protected final boolean[] isColumnStringTypeCache;

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

    public ClientStorage(String path) throws Exception
    {
        this.path = path;

        final File file = new File(path);
        if (!file.exists())
            throw new MissingFileException(path);

        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel channel = randomAccessFile.getChannel();

        // Create a little Endian Buffer
        final long size = channel.size();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        channel.close();
        randomAccessFile.close();

        // Read Header
        // Checks the Magic String
        final byte[] magic = new byte[getMagicSig().length()];
        buffer.get(magic);
        buffer.position(getMagicSig().length());
        if (!new String(magic).equals(getMagicSig()))
            throw new InvalidDataException(path, getMagicSig());

        // The Storage file needs at least a header
        if (getHeaderSize() > size)
            throw new CorruptedFileException(path);

        // Reads default header
        recordsCount = buffer.getInt();
        fieldsCount = buffer.getInt();
        recordSize = buffer.getInt();
        stringBlockSize = buffer.getInt();

        // Finish header reading of child classes
        finishHeaderReading();

        // Read Data
        buffer.position(getDataBlockOffset());

        // Map indexes to row offsets
        List<Integer> keys = new LinkedList<>();
        // Get indexes of fields marked as key
        for (final Field field : getAllAnnotatedFields())
        {
            final ClientStorageEntry annotation = field.getAnnotation(ClientStorageEntry.class);
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

    // Overwritten Methods
    protected abstract int getHeaderSize();

    protected abstract String getMagicSig();

    protected abstract String getExtension();

    protected abstract T create();

    protected void finishHeaderReading()
    {
    }

    // Getter
    public int getRecordsCount()
    {
        return recordsCount;
    }

    public int getFieldsCount()
    {
        return fieldsCount;
    }

    protected int getRecordSize()
    {
        return recordSize;
    }

    protected int getStringTableSize()
    {
        return stringBlockSize;
    }

    protected int getFieldSize()
    {
        return recordSize / fieldsCount;
    }

    protected int getDataBlockOffset()
    {
        return getHeaderSize();
    }

    protected int getStringBlockOffset()
    {
        return getDataBlockOffset() + getRecordsCount() * getRecordSize();
    }

    protected boolean isFieldPossibleString(int x)
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
                ClientStorageEntry.class, true);
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

        builder.append(path).append(" =\n");

        builder.append("{\n");

        for (int y = 0; y < recordsCount; ++y)
        {
            builder.append("    {");
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

        builder.delete(builder.length() - (",\n".length()), builder.length()).append("\n");

        builder.append("};");
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
            final ClientStorageEntry annotation = field.getAnnotation(ClientStorageEntry.class);

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
