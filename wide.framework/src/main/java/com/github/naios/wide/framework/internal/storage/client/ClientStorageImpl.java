
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.storage.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.github.naios.wide.api.config.schema.Schema;
import com.github.naios.wide.api.config.schema.TableSchema;
import com.github.naios.wide.api.framework.storage.client.ClientStorage;
import com.github.naios.wide.api.framework.storage.client.ClientStorageException;
import com.github.naios.wide.api.framework.storage.client.ClientStorageFormat;
import com.github.naios.wide.api.framework.storage.client.ClientStoragePolicy;
import com.github.naios.wide.api.framework.storage.client.ClientStorageStructure;
import com.github.naios.wide.api.util.FormatterWrapper;
import com.github.naios.wide.api.util.StringUtil;
import com.github.naios.wide.framework.internal.FrameworkServiceImpl;
import com.google.common.reflect.TypeToken;

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
class MissingSchemaException extends ClientStorageException
{
    public MissingSchemaException(final String path)
    {
        super(String.format("Didn't find any schema matching storage %s.", path));
    }
}

public abstract class ClientStorageImpl<T extends ClientStorageStructure>
    implements ClientStorageDataTable<T>, ClientStorage<T>
{
    private final String path;

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
    private ClientStorageDataTable<T> dataTable;

    public ClientStorageImpl(final String path) throws ClientStorageException
    {
        this (path, ClientStoragePolicy.DEFAULT_POLICY);
    }

    public ClientStorageImpl(final String path, final ClientStoragePolicy policy) throws ClientStorageException
    {
        this.path = path;

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
                buffer = channel
                        .map(FileChannel.MapMode.READ_ONLY, 0, size)
                        .order(ByteOrder.LITTLE_ENDIAN);
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
        Schema schema = null;

        if (policy.isSchemaProvided())
            schema = FrameworkServiceImpl.getConfig().getActiveEnviroment().getClientStorageConfig().schema().get();

        if (Objects.nonNull(schema))
        {
            try
            {
                final TableSchema tableSchema = schema.getSchemaOf(file.getName());
                dataTable = new KnownSchemaDataTable<>(this, tableSchema, buffer);
                return;
            }
            catch (final Exception e)
            {
            }
        }

        if (!policy.isSchemaEstimated())
            throw new MissingSchemaException(path);

        dataTable = new UnknownSchemaDataTable<>(this, buffer);
     }

    public static String getPathForStorage(final String path)
    {
        return FrameworkServiceImpl.getConfig().getActiveEnviroment().getClientStorageConfig().path().get() + "/" + path;
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
    @Override
    public int getRecordsCount()
    {
        return recordsCount;
    }

    @Override
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

    protected int getDataBlockOffset()
    {
        return getHeaderSize();
    }

    protected int getStringBlockOffset()
    {
        return getDataBlockOffset() + getRecordsCount() * getRecordSize();
    }

    @Override
    public List<String> getFieldNames()
    {
        return dataTable.getFieldNames();
    }

    @Override
    public List<String> getFieldDescription()
    {
        return dataTable.getFieldDescription();
    }

    @Override
    public List<TypeToken<?>> getFieldType()
    {
        return dataTable.getFieldType();
    }

    protected ClientStorageStringTable getStringTable()
    {
        return stringTable;
    }

    @Override
    public T getEntry(final int entry) throws ClientStorageException
    {
        return dataTable.getEntry(entry);
    }

    @Override
    public ClientStorageFormat getFormat()
    {
        return dataTable.getFormat();
    }

    /**
     * @return ClientStorage as Object Array (use toString() to get Content)
     */
    @Override
    public Object[][] asObjectArray()
    {
        return dataTable.asObjectArray();
    }

    /**
     * @return ClientStorage as String Array
     */
    @Override
    public String[][] asStringArray()
    {
        final Object[][] asObjects = asObjectArray();
        final int height = asObjects.length, width = asObjects[0].length;

        final String[][] array = new String[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                array[y][x] = new FormatterWrapper(asObjects[y][x]).toString();

        return array;
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s) Storage: %s\n%s\n\n%s\n%s",
                getExtension(), getMagicSig(), path, getFormat(), StringUtil.concat(" | ", getFieldNames()),
                    Arrays.deepToString(asStringArray()).replaceAll("],", "],\n"));
    }

    @Override
    public Iterator<T> iterator()
    {
        return dataTable.iterator();
    }
}
