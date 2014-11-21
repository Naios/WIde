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
    public InvalidDataException(String path)
    {
        super(String.format("File '%s' isn't valid DBC file!", path));
    }
}

@SuppressWarnings("serial")
class CorruptedFileException extends Exception
{
    public CorruptedFileException(String path)
    {
        super(String.format("DBC File '%s' seems to be corrupted!", path));
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

public abstract class Storage
{
    protected final int recordsCount /*Y*/, fieldsCount /*X*/, recordSize, stringBlockSize;

    protected final ByteBuffer buffer;

    protected final int[][] m_rows;

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
            throw new InvalidDataException(path);

        if (getHeaderSize() > size)
            throw new CorruptedFileException(path);

        recordsCount = buffer.getInt();
        fieldsCount = buffer.getInt();
        recordSize = buffer.getInt();
        stringBlockSize = buffer.getInt();

        m_rows = new int[recordsCount][];
    }

    public abstract int getHeaderSize();

    public abstract String getMagicSig();

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

    public int[] GetRowAsArray(int row)
    {
        return m_rows[row];
    }

    protected int getFieldSize()
    {
        return recordSize / fieldsCount;
    }
}
