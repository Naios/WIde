package wide.core.framework.storage.client;

/**
 * Implementation of Blizzards ADB Cache files as described in:
 * http://www.pxr.dk/wowdev/wiki/index.php?title=ADB
 */
public class ADBStorage<T extends ClientStorageStructure> extends DB2Storage<T>
{
    private final static String MAGIC = "WCH2";

    protected final static String EXTENSION = ".adb";

    public ADBStorage(Class<? extends ClientStorageStructure> type, String path) throws Exception
    {
        super(type, path);
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
