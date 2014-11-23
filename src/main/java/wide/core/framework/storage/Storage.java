package wide.core.framework.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
class InvalidDataException extends Exception
{
    public InvalidDataException(String path, String magic)
    {
        super(String.format("File '%s' isn't valid %s file!", path, magic));
    }
}

@SuppressWarnings("serial")
class CorruptedFileException extends Exception
{
    public CorruptedFileException(String path)
    {
        super(String.format("Storage File '%s' seems to be corrupted!", path));
    }
}

@SuppressWarnings("serial")
class MissingFileException extends Exception
{
    public MissingFileException(String path)
    {
        super(String.format("File '%s' isn't existing!", path));
    }
}

public abstract class Storage<T> implements Iterable<T>
{
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

    protected final Map<Integer, String> stringTable = new HashMap<>();

    public Storage(String path) throws Exception
    {
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

        // Reads header
        recordsCount = buffer.getInt();
        fieldsCount = buffer.getInt();
        recordSize = buffer.getInt();
        stringBlockSize = buffer.getInt();
    }

    protected abstract int getHeaderSize();

    protected abstract String getMagicSig();

    public int getRecordsCount()
    {
        return recordsCount;
    }

    public int getFieldsCount()
    {
        return fieldsCount;
    }

    public int getRecordSize()
    {
        return recordSize;
    }

    public int getStringTableSize()
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
}
