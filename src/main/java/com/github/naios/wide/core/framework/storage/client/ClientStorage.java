
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.framework.storage.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.storage.mapping.schema.SchemaCache;
import com.github.naios.wide.core.framework.storage.mapping.schema.TableSchema;

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

public abstract class ClientStorage<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>
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
     * Our strings in the ByteBuffer
     */
    private final ClientStorageStringTable stringTable;

    /**
     * Our strings in the ByteBuffer
     */
    private final ClientStorageDataTable<T> dataTable;

    public ClientStorage(final String path) throws ClientStorageException
    {
        final File file = new File(path);
        if (!file.exists())
            throw new MissingFileException(path);

        // Create the byte buffer
        final long size;

        final ByteBuffer buffer;
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
        finishHeaderReading(buffer);

        stringTable = new ClientStorageStringTable(buffer, getStringBlockOffset());

        // Select our Schema
        final TableSchema schema = SchemaCache.INSTANCE.get(WIde.getConfig().get().getActiveEnviroment()
                .getClientStorageConfig().schema().get()).getSchemaOf(file.getName());

        if (schema == null)
        {
            // TODO use default schema for unknown structures
            throw new Error("no schema!");
        }

        // TODO init data table dependent whether format exists or not
        dataTable = null;
    }

    // Overwritten Methods
    protected abstract int getHeaderSize();

    protected abstract String getMagicSig();

    protected abstract String getExtension();

    /**
     * may be overwritten by classes that inherit this one
     */
    protected void finishHeaderReading(final ByteBuffer buffer)
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

    @Override
    public String[] getFieldName()
    {
        return dataTable.getFieldName();
    }

    @Override
    public String[] getFieldDescription()
    {
        return dataTable.getFieldDescription();
    }

    @Override
    public Class<?>[] getFieldType()
    {
        return dataTable.getFieldType();
    }

    protected ClientStorageStringTable getStringTable()
    {
        return stringTable;
    }

    protected int getOffset(final int y, final int x)
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

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        return dataTable.getEntry(entry);
    }

    public void fillNameStorage(final Map<Integer, String> map, final int entryColumn, final int nameColumn)
    {
        // TODO
    }

    /**
     * @return ClientStorage as Object Array (use toString() to get Content)
     */
    public Object[][] asObjectArray()
    {
        return asObjectArray(false);
    }

    @Override
    public Object[][] asObjectArray(final boolean prettyWrap)
    {
        return dataTable.asObjectArray(prettyWrap);
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
        return dataTable.iterator();
    }
}
