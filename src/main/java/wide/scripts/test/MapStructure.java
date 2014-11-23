package wide.scripts.test;

import wide.core.framework.storage.DBCStructure;
import wide.core.framework.storage.StorageEntry;

public interface MapStructure extends DBCStructure
{
    public int getMapId();
    
    public String getName();
}
