package wide.core.framework.storage;

import wide.core.framework.game.GameBuild;
import wide.core.framework.game.GameBuildMask;

public abstract class GameBuildDependentStorageStructure
{
    private final GameBuildMask gamebuildMask;

    public final static GameBuildMask ALL_BUILDS = new GameBuildMask().add_all();

    public GameBuildDependentStorageStructure()
    {
        this(ALL_BUILDS);
    }

    public GameBuildDependentStorageStructure(final GameBuildMask gamebuildMask)
    {
        this.gamebuildMask = gamebuildMask;
    }

    public boolean matchesGameBuild(final GameBuild build)
    {
        return gamebuildMask.contains(build);
    }
}
