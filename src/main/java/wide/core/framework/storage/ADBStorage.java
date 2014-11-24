package wide.core.framework.storage;

/**
 * Implementation of Blizzards ADB Cache files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=ADB
 */
public abstract class ADBStorage<T> extends DB2Storage<T>
{
    private final static String MAGIC = "WCH2";

    private final static String EXTENSION = ".adb";

    public ADBStorage(String path) throws Exception
    {
        super(path);
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
