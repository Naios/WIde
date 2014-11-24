package wide.core.framework.storage.client;

public class ClientStorageSelector<T>
{
    private final String path;

    private final ClientStorageStructureCreate<T> create;

    public ClientStorageSelector(String path, ClientStorageStructureCreate<T> create)
    {
        this.path = path;
        this.create = create;
    }

    public ClientStorage<T> select() throws Exception
    {
        // TODO improve this
        final String extension = path.substring(path.lastIndexOf("."), path.length());

        switch (extension)
        {
            case ADBStorage.EXTENSION:
                return new ADBStorage<T>(path)
                {
                    @Override
                    protected T create()
                    {
                        return create.create();
                    }
                };
            case DB2Storage.EXTENSION:
                return new DB2Storage<T>(path)
                {
                    @Override
                    protected T create()
                    {
                        return create.create();
                    }
                };
            case DBCStorage.EXTENSION:
                return new DBCStorage<T>(path)
                {
                    @Override
                    protected T create()
                    {
                        return create.create();
                    }
                };
            default:
                return null;
        }
    }
}
