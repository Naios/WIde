package wide.core.framework.storage.client;

import wide.core.framework.storage.StorageException;

@SuppressWarnings("serial")
public class ClientStorageException extends StorageException
{
    public ClientStorageException(final String msg)
    {
        super(msg);
    }
}
