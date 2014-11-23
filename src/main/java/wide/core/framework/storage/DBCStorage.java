package wide.core.framework.storage;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
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

// TODO Implement this
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
public abstract class DBCStorage<T> extends Storage<T>
    implements StorageFactory<DBCStructure>
{
    private final static int HEADER_SIZE = 20;
    private final static String MAGIC = "WDBC";

    private final Map<Integer, Integer> intToOffsetCache = new HashMap<>();

    public DBCStorage(String path) throws Exception
    {
        super(path);

        // Pre process data
        // Map indexes to row offsets
        List<Integer> keys = new LinkedList<>();
        // Get indexes of fields marked as key
        for (final Field field : create().getClass().getFields())
        {
            StorageEntry annotation = field.getAnnotation(StorageEntry.class);
            if (annotation == null)
                continue;

            if (annotation.key())
                keys.add(annotation.idx());
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
            intToOffsetCache.put(entry, getOffset(i, 0));
        }
            
            
        
        // TODO Pre generate String Table

        /*
        public Object getData(int col, int row)
        {
            final int off = getRecordSize() * row + (col * rl);
            return getWord(off);
        }
        */

        /*
        for (int y = 0; y < recordsCount; ++y)
        {
            m_rows[y] = new int[fieldsCount];
            for (int x = 0; x < fieldsCount; ++x)
                m_rows[y][x] = buffer.getInt();


        }
        */

        /*
        // Store Records
        for (int i = 0; i < recordsCount; i++)
        {
            m_rows[i] = new int[fieldsCount];
            buffer.get(m_rows[i]);


            break;
        }
        */
        // Store Strings
    }

    private int getOffset(int y, int x) throws OutOfBoundsException
    {
        if ((y < 0 || y >= recordsCount) ||
            (x < 0 || x >= fieldsCount))
            throw new OutOfBoundsException();

        return (y * recordSize) + (x * getFieldSize());
    }

    @Override
    public int getHeaderSize()
    {
        return HEADER_SIZE;
    }

    @Override
    public String getMagicSig()
    {
        return MAGIC;
    }

    @Override
    public String toString()
    {
        
        final StringBuilder builder = new StringBuilder();
        // for (int y = 0; y < recordsCount; ++y)
           //  builder.append(Arrays.toString(m_rows[y])).append("\n");

        return builder.toString();
    }

    
    
    public T getEntry(int entry)
    {
        final int offset = intToOffsetCache.get(entry);
        if (offset == 0)
            return null;
        else
            return getEntryByOffset(offset);
    }

    private T getEntryByOffset(int offset)
    {
        @SuppressWarnings("unchecked")
        T record = (T) create();

        for (final Field field : create().getClass().getFields())
        {
            StorageEntry annotation = field.getAnnotation(StorageEntry.class);
            if (annotation == null)
                continue;
            
            // TODO Implement String table lookup
            
            field.setInt(entry, buffer.getInt());
        }
        return record;
    }

    @Override
    public Iterator<T> iterator()
    {
        // TODO Implement this
        return null;
    }
}
