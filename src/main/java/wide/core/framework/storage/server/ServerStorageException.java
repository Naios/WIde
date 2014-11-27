package wide.core.framework.storage.server;

import wide.core.framework.storage.StorageException;

@SuppressWarnings("serial")
public class ServerStorageException extends StorageException
{
    public ServerStorageException(final String msg)
    {
        super(msg);
    }
}
