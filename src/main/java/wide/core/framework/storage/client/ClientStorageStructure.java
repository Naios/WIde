package wide.core.framework.storage.client;

import java.lang.annotation.Annotation;

import wide.core.framework.game.GameBuildMask;
import wide.core.framework.storage.GameBuildDependentStorageStructure;

public abstract class ClientStorageStructure extends GameBuildDependentStorageStructure
{
    private final String regex;

    public final static String REGEX_MATCH_ALL = ".*";

    protected ClientStorageStructure()
    {
        this(REGEX_MATCH_ALL);
    }

    protected ClientStorageStructure(final String mask)
    {
        this(GameBuildDependentStorageStructure.ALL_BUILDS, mask);
    }

    protected ClientStorageStructure(final GameBuildMask gamebuild)
    {
        this(gamebuild, REGEX_MATCH_ALL);
    }

    protected ClientStorageStructure(final GameBuildMask gamebuilds, final String mask)
    {
        super(gamebuilds);
        this.regex = mask;
    }

    public boolean matchesFile(final String path)
    {
        return path.matches(regex);
    }

    @Override
    protected Class<? extends Annotation> getSpecificAnnotation()
    {
        return ClientStorageEntry.class;
    }
}
