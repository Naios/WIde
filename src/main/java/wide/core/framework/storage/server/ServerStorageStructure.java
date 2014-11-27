package wide.core.framework.storage.server;

import java.lang.annotation.Annotation;

import wide.core.framework.game.GameBuildMask;
import wide.core.framework.storage.GameBuildDependentStorageStructure;

public abstract class ServerStorageStructure extends GameBuildDependentStorageStructure
{
    private final String tableName;

    public ServerStorageStructure(final String tableName)
    {
        this(GameBuildDependentStorageStructure.ALL_BUILDS, tableName);
    }

    public ServerStorageStructure(final GameBuildMask gamebuilds, final String tableName)
    {
        super(gamebuilds);
        this.tableName = tableName;
    }

    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ServerStorageEntry.class;
    }

    public String getTableName()
    {
        return tableName;
    }
}
