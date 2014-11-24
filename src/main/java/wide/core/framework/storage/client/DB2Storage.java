package wide.core.framework.storage.client;

/**
 * Implementation of Blizzards DB2 files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DB2
 */
public abstract class DB2Storage<T> extends ClientStorage<T>
{
    private final static int HEADER_SIZE = 48;

    private final static String MAGIC = "WDB2";

    private final static String EXTENSION = ".db2";

    protected int tableHash, timestampLastWritten, minId, maxId, locale, unk2;

    public DB2Storage(String path) throws Exception
    {
        super(path);
    }

    @Override
    protected void finishHeaderReading()
    {
        tableHash = buffer.getInt();
        timestampLastWritten = buffer.getInt();
        minId = buffer.getInt();
        maxId = buffer.getInt();
        locale = buffer.getInt();
        unk2 = buffer.getInt();
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

    @Override
    protected String getExtension()
    {
        return EXTENSION;
    }

    public int getTableHash()
    {
        return tableHash;
    }

    public int getMinId()
    {
        return minId;
    }

    public int getMaxId()
    {
        return maxId;
    }

    public int getLocale()
    {
        return locale;
    }
}
