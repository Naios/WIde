package wide.scripts.test;

import wide.core.framework.storage.client.ClientStorageEntry;
import wide.core.framework.storage.client.ClientStorageStructure;

public class TaxiNodesStructure extends ClientStorageStructure
{
    public TaxiNodesStructure()
    {
        super(".*TaxiNodes.db2");
    }

    @ClientStorageEntry(idx=0, key=true)
    private int entry;

    @ClientStorageEntry(idx=1)
    private int map;

    @ClientStorageEntry(idx=2)
    private float x;

    @ClientStorageEntry(idx=3)
    private float y;

    @ClientStorageEntry(idx=4)
    private float z;
}
