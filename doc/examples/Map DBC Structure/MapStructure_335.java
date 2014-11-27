package wide.scripts.test;

import wide.core.framework.game.GameBuild;
import wide.core.framework.game.GameBuildDependency;
import wide.core.framework.storage.client.ClientStorageEntry;

@GameBuildDependency(build=GameBuild.V3_3_5a_12340)
public class MapStructure_335 implements MapStructure
{
    @ClientStorageEntry(idx=0, name="Map ID", key=true)
    private int mapId;

    @ClientStorageEntry(idx=1, name="Map Name")
    private String name;

    @Override
    public int getMapId()
    {
        return mapId;
    }

    @Override
    public String getName()
    {
        return name;
    }
}