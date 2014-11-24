package wide.scripts.printdbc;

import wide.core.framework.storage.client.ClientStorageEntry;

public class UnknownStructure
{
    @ClientStorageEntry(idx=0, name="Unknown Entry", key=true)
    private int entry;

    public int getEntry()
    {
        return entry;
    }
}
