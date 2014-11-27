package wide.core.framework.storage.client;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wide.core.Constants;
import wide.core.framework.game.GameBuildMask;
import wide.core.framework.storage.GameBuildDependentStorageStructure;
import wide.core.framework.util.ClassUtil;

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
    public String toString()
    {
        final Field[] all_fields = ClassUtil.getAnnotatedDeclaredFields(getClass(),
                ClientStorageEntry.class, true);

        final List<String> list = new LinkedList<String>();

        for (final Field field : all_fields)
        {
            if (!field.isAccessible())
                field.setAccessible(true);

            final StringBuilder builder = new StringBuilder()
                .append(field.getName())
                .append("=");

            try
            {
                builder.append(field.get(this).toString());
            }
            catch (final Exception e)
            {
                builder.append(Constants.STRING_EXCEPTION);
            }

            list.add(builder.toString());
        }

        return Arrays.toString(list.toArray());
    }
}
