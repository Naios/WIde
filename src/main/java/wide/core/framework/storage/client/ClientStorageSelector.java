package wide.core.framework.storage.client;

public class ClientStorageSelector<T extends ClientStorageStructure>
{
    private final String path;

    private final Class<? extends ClientStorageStructure> type;

    public ClientStorageSelector(final Class<? extends ClientStorageStructure> type, final String path)
    {
        this.path = path;
        this.type = type;
    }

    public ClientStorage<T> select() throws ClientStorageException
    {
        // TODO improve this: maybe there is an easier way to get the extension
        final String extension = path.substring(path.lastIndexOf("."), path.length());

        switch (extension)
        {
            case ADBStorage.EXTENSION:
                return new ADBStorage<T>(type, path);
            case DB2Storage.EXTENSION:
                return new DB2Storage<T>(type, path);
            case DBCStorage.EXTENSION:
                return new DBCStorage<T>(type, path);
            default:
                return null;
        }
    }
}