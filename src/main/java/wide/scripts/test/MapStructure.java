package wide.scripts.test;

import wide.core.framework.storage.DBCStructure;
import wide.core.framework.storage.StorageEntry;

public class MapStructure extends DBCStructure
{
    @StorageEntry(idx=0, key=true, name="Map id")
    private int mapid;

    public int getMapid()
    {
        return mapid;
    }
}
