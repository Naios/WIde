package wide.core.framework.storage;

import java.util.Arrays;


/**
 * Implementation of Blizzards DBC files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DBC
 */
public class DBCStorage extends Storage
{
    private final static int HEADER_SIZE = 20;
    private final static String MAGIC = "WDBC";

    public DBCStorage(String path) throws Exception
    {
        super(path);

        /*
        public Object getData(int col, int row)
        {
            final int off = getRecordSize() * row + (col * rl);
            return getWord(off);
        }
        */

        for (int y = 0; y < recordsCount; ++y)
        {
            m_rows[y] = new int[fieldsCount];
            for (int x = 0; x < fieldsCount; ++x)
                m_rows[y][x] = buffer.getInt();


        }

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

    private int getOffset(int y, int x)
    {
        return (y * fieldsCount) + (x * getFieldSize());
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
        for (int y = 0; y < recordsCount; ++y)
            builder.append(Arrays.toString(m_rows[y])).append("\n");

        return builder.toString();
    }
}
