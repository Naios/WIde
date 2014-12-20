
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValue;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.mapping.JsonMapper;
import com.github.naios.wide.core.framework.storage.mapping.Mapper;
import com.github.naios.wide.core.framework.storage.mapping.schema.SchemaCache;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;
import com.github.naios.wide.core.framework.util.FormatterWrapper;
import com.github.naios.wide.core.framework.util.Pair;

@SuppressWarnings("serial")
class InvalidDataException extends ClientStorageException
{
    public InvalidDataException(final String path, final String magic)
    {
        super(String.format("File '%s' isn't valid %s file!", path, magic));
    }
}

@SuppressWarnings("serial")
class CorruptedFileException extends ClientStorageException
{
    public CorruptedFileException(final String path)
    {
        super(String.format("Storage File '%s' seems to be corrupted!", path));
    }
}

@SuppressWarnings("serial")
class MissingFileException extends ClientStorageException
{
    public MissingFileException(final String path)
    {
        super(String.format("File '%s' isn't existing!", path));
    }
}

@SuppressWarnings("serial")
class OutOfBoundsException extends ClientStorageException
{
    public OutOfBoundsException()
    {
        super("Range is out of Bounds!");
    }
}

@SuppressWarnings("serial")
class KeyIsNoIntException extends ClientStorageException
{
    public KeyIsNoIntException()
    {
        super("Given DBC Structure has a Key assigned that isn't an int, impossible!");
    }
}

@SuppressWarnings("serial")
class MissingKeyException extends ClientStorageException
{
    public MissingKeyException()
    {
        super("Given Client Storage Structure has no Key assigned!");
    }
}

@SuppressWarnings("serial")
class MissingEntryException extends ClientStorageException
{
    public MissingEntryException()
    {
        super("Entry <unknown> is missing in the storage!");
    }

    public MissingEntryException(final int entry)
    {
        super(String.format("Entry %s is missing in the storage!", entry));
    }
}

@SuppressWarnings("serial")
class NoMatchedStructureException extends ClientStorageException
{
    public NoMatchedStructureException(final Class<? extends ClientStorageStructure> type, final String  mask, final String path)
    {
        super(String.format("Given Client Storage Structure mask in class %s (%s) does not match to file %s.", type.getName(), mask, path));
    }
}

@SuppressWarnings("serial")
class WrongStructureException extends ClientStorageException
{
    public WrongStructureException(final Class<? extends ClientStorageStructure> type, final String path)
    {
        super(String.format("Given Client Storage Structure %s is not valid for file %s.", type.getName(), path));
    }
}

@SuppressWarnings("serial")
class MappingFailedException extends ClientStorageException
{
    public MappingFailedException()
    {
        super("Failed with mapping or class creation!");
    }
}

public abstract class ClientStorage<T extends ClientStorageStructure> implements Iterable<T>
{
    protected final static int FLOAT_CHECK_BOUNDS = 100000000;

    protected final static float FLOAT_CHECK_PERCENTAGE = 0.95f;

    protected final static int STRING_CHECK_MAX_RECORDS = 5;

    /**
     * The count of records (<b>Y / Rows</b>) of the Storage
     */
    private final int recordsCount;

    /**
     * The count of fields (<b>X / Columns</b>) of the Storage
     */
    private final int fieldsCount /*X*/;

    /**
     * The overall size of the record (size of all Fields together).<p>
     * Use {@link #getFieldSize()} the size of a single field.
     */
    private final int recordSize;

    /**
     * The size of the String Block.
     */
    private final int stringBlockSize;

    /**
     * Out ByteBuffer that holds the data
     */
    private final ByteBuffer buffer;

    /**
     * Our strings in the ByteBuffer
     */
    private final ClientStorageStringTable strings;

    /**
     * The mapper that maps our raw dbc data to proxys
     */
    private final Mapper<Pair<ByteBuffer, ClientStorageStringTable>, T, ObservableValue<?>> mapper;

    /**
     * Maps entries to offsets
     */
    private final Map<Integer, Integer> entryToOffsetCache = new HashMap<>();

    // TODO remove this
    private final ClientStoragePossibleFieldChecker[] fieldType;


    public ClientStorage(final String path) throws ClientStorageException
    {
        final File file = new File(path);
        if (!file.exists())
            throw new MissingFileException(path);

        // Create the byte buffer
        final long size;

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"))
        {
            try (FileChannel channel = randomAccessFile.getChannel())
            {
                // Create a little Endian Buffer
                size = channel.size();
                buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .asReadOnlyBuffer();
            }
            catch (final Exception e)
            {
                throw e;
            }
        }
        catch (final Exception e)
        {
            throw new MissingFileException(path);
        }

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

        strings = new ClientStorageStringTable(buffer, getStringBlockOffset());

        // Select our Schema
        final TableSchema schema = SchemaCache.INSTANCE.get(WIde.getConfig().get().getActiveEnviroment()
                .getClientStorageConfig().schema().get()).getSchemaOf(file.getName());

        if (schema == null)
        {
            // TODO use default schema for unknown structures
            throw new Error("no schema!");
        }

        // Create Mapper
        mapper = new JsonMapper<>(schema, Arrays.asList(ClientStoragePrivateBase.class),
                ClientStorageBaseImplementation.class);

        // Read Data
        buffer.position(getDataBlockOffset());

        // Map indexes to row offsets
        final List<Integer> keys = new ArrayList<>();

        // mapper.getPlan().getKeys().forEach(entry -> keys.add(entry.));

        // Structure needs to define at least 1 key
        if (keys.isEmpty())
            throw new MissingKeyException();

        if (keys.size() > 1)
            throw new UnsupportedOperationException();

        for (int i = 0; i < recordsCount; ++i)
        {
            // Gets the entry
            final int entry = buffer.getInt(getOffset(i, keys.get(0)));

            // Store it with its offset
            entryToOffsetCache.put(entry, getOffset(i, 0));
        }

        // Calculate field types
        fieldType = new ClientStoragePossibleFieldChecker[fieldsCount];
        Arrays.fill(fieldType, ClientStoragePossibleFieldChecker.UNKNOWN);

        // Pre calculate type based on given mapping structure
        for (int x = 0; x < fieldsCount; ++x)
        {
            final Field field = getFieldForColumn(x);
            if (field != null)
            {
                for (final ClientStoragePossibleFieldChecker t : ClientStoragePossibleFieldChecker.values())
                    if (field.getType().isAssignableFrom(t.getType()))
                    {
                        fieldType[x] = t;
                        break;
                    }

                // If the column is a string but the given structure is wrong throw
                // an exception
                if (fieldType[x].equals(ClientStoragePossibleFieldChecker.STRING))
                    if (ClientStoragePossibleFieldChecker.STRING.check(this, x))
                        throw new WrongStructureException(type, path);
            }
            else
                for (final ClientStoragePossibleFieldChecker f : ClientStoragePossibleFieldChecker.values())
                    if (f.check(this, x))
                    {
                        fieldType[x] = f;
                        break;
                    }
        }
    }

    // Overwritten Methods
    protected abstract int getHeaderSize();

    protected abstract String getMagicSig();

    protected abstract String getExtension();

    /**
     * may be overwritten by classes that inherit this one
     */
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

    protected ClientStoragePossibleFieldChecker getFieldType(final int field)
    {
        return fieldType[field];
    }

    public ClientStoragePossibleFieldChecker[] getFieldTypes()
    {
        return fieldType;
    }

    protected ByteBuffer getByteBuffer()
    {
        return buffer;
    }

    public ClientStorageStringTable getStringTable()
    {
        return strings;
    }

    int getOffset(final int y, final int x)
    {
        if ((y < 0 || y >= recordsCount) || (x < 0 || x >= fieldsCount))
        {
            // Should never occur
            assert false;
            return 0;
        }

        return getHeaderSize() + (y * recordSize) + (x * getFieldSize());
    }

    private int getFieldOfOffset(final int offset)
    {
        return (offset % getRecordSize()) / getFieldSize();
    }

    private String getStringAtRelativeOffset(final int relativeOffset)
    {
        return getStringAtOffset(buffer.getInt(relativeOffset) + getStringBlockOffset());
    }

    public T getEntry(final int entry) throws ClientStorageException
    {
        final int offset = entryToOffsetCache.get(entry);
        if (offset == 0)
            throw new MissingEntryException(entry);
        else
            try
            {
                return getEntryByOffset(offset);
            }
            catch (final MissingEntryException e)
            {
                throw new MissingEntryException(entry);
            }
    }

    @SuppressWarnings("unchecked")
    private T getEntryByOffset(final int offset) throws ClientStorageException
    {
        if (offset >= getStringBlockOffset())
            throw new MissingEntryException();

        final ClientStorageStructure record;
        try
        {
            record = type.newInstance();
        }
        catch (final Exception e)
        {
            throw new MappingFailedException();
        }

        for (final Field field : getAllAnnotatedFields())
        {
            final ClientStorageEntry annotation = field.getAnnotation(ClientStorageEntry.class);

            final int absolut_index = offset + (annotation.idx() * getFieldSize());

            if (!field.isAccessible())
                field.setAccessible(true);

            try
            {
                field.set(record, getObjectForOffsetAndField(absolut_index, getFieldOfOffset(absolut_index)));
            }
            catch (final Exception e)
            {
                throw new MappingFailedException();
            }
        }
        return (T)record;
    }

    /**
     * @return entry at the offset as Object
     */
    protected Object getObjectForOffsetAndField(final int offset, final int field)
    {
        switch (getFieldType(field))
        {
            case BOOLEAN:
                return new Boolean((buffer.getInt(offset) != 0) ? true : false);
            case FLOAT:
                return new Float(buffer.getFloat(offset));
            case STRING:
                return getStringAtRelativeOffset(offset);
            case INTEGER:
            case UNKNOWN:
            default:
                return new Integer(buffer.getInt(offset));
        }
    }

    /**
     * @return ClientStorage as Int Array
     */
    public int[][] asIntArray()
    {
        final int[][] array = new int[recordsCount][fieldsCount];
        for (int y = 0; y < recordsCount; ++y)
            for (int x = 0; x < fieldsCount; ++x)
                array[y][x] = buffer.getInt(getOffset(y, x));

        return array;
    }

    public void fillNameStorage(final Map<Integer, String> map, final int entryColumn, final int nameColumn)
    {
        for (int y = 0; y < recordsCount; ++y)
        {
            map.put((Integer) getObjectForOffsetAndField(getOffset(y, entryColumn), entryColumn),
                    (String) getObjectForOffsetAndField(getOffset(y, nameColumn), nameColumn));
        }
    }

    /**
     * @return ClientStorage as Object Array (use toString() to get Content)
     */
    public Object[][] asObjectArray()
    {
        return asObjectArray(false);
    }

    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        final Object[][] array = new Object[recordsCount][fieldsCount];
        for (int y = 0; y < recordsCount; ++y)
            for (int x = 0; x < fieldsCount; ++x)
            {
                final Object obj = getObjectForOffsetAndField(getOffset(y, x), x);
                if (prettyWrap)
                    array[y][x] = new FormatterWrapper(obj);
                else
                    array[y][x] = obj;
            }

        return array;
    }

    /**
     * @return ClientStorage as String Array
     */
    public String[][] asStringArray()
    {
        final Object[][] asObjects = asObjectArray();
        final String[][] array = new String[recordsCount][fieldsCount];
        for (int y = 0; y < recordsCount; ++y)
            for (int x = 0; x < fieldsCount; ++x)
                array[y][x] = asObjects[y][x].toString();

        return array;
    }

    @Override
    public String toString()
    {
        return Arrays.deepToString(asObjectArray(true)).replaceAll("],", "],\n");
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
                try
                {
                    return getEntryByOffset(getOffset(idx++, 0));
                }
                catch (final Exception e)
                {
                    return null;
                }
            }
        };
    }
}
