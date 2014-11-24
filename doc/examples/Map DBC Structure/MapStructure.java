package wide.scripts.test;

import wide.core.framework.storage.client.ClientStorageStructure;

@ClientStorageStructure(filename="Map.dbc")
public interface MapStructure
{
    public int getMapId();

    public String getName();
}
