package wide.scripts.test;

import wide.core.framework.storage.DBCStructure;
import wide.core.framework.storage.StorageEntry;

public class MapStructure extends DBCStructure
{
    @StorageEntry(idx=1, key=true, name="Map id")
    public int key;

    @Override
    public DBCStructure create()
    {
        return new MapStructure();
    }
}
