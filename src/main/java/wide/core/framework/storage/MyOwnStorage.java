package wide.core.framework.storage;

import java.io.DataInputStream;
import java.io.FileInputStream;
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

public abstract class MyOwnStorage
{
    protected final int recordsCount, fieldsCount, recordSize, stringTableSize;

    protected final Map<Integer, String> stringTable = new HashMap<>();

    protected final DataInputStream istream;

    protected byte[][] m_rows;

    public MyOwnStorage(String path) throws Exception
    {
        istream = new DataInputStream(new FileInputStream(path));

        /*
        if (reader.BaseStream.Length < HeaderSize)
        {
            throw new InvalidDataException(String.Format("File {0} is corrupted!", fileName));
        }
        */

        if (istream.readInt() != getDBCFmtSig())
            throw new InvalidDataException(path);

        recordsCount = istream.readInt();
        fieldsCount = istream.readInt();
        recordSize = istream.readInt();
        stringTableSize = istream.readInt();
    }

    public abstract int getHeaderSize();

    public abstract int getDBCFmtSig();

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
        return stringTableSize;
    }

    public byte[] GetRowAsByteArray(int row)
    {
        return m_rows[row];
    }
}
