package wide.core.framework.storage.client;

/**
 * Implementation of Blizzards DBC files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=DBC
 */
public abstract class DBCStorage<T> extends ClientStorage<T>
{
    private final static int HEADER_SIZE = 20;

    private final static String MAGIC = "WDBC";

    private final static String EXTENSION = ".dbc";

    public DBCStorage(String path) throws Exception
    {
        super(path);
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
}
