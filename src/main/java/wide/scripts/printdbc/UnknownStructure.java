package wide.scripts.printdbc;

import wide.core.framework.storage.client.ClientStorageEntry;
import wide.core.framework.storage.client.ClientStorageStructure;

public class UnknownStructure extends ClientStorageStructure
{
    @ClientStorageEntry(idx=0, name="Unknown Entry", key=true)
    private int entry;

    public int getEntry()
    {
        return entry;
    }
}
