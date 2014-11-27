package wide.core.framework.storage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wide.core.Constants;
import wide.core.framework.game.GameBuild;
import wide.core.framework.game.GameBuildMask;
import wide.core.framework.util.ClassUtil;

public abstract class GameBuildDependentStorageStructure
{
    private final GameBuildMask gamebuildMask;

    public final static GameBuildMask ALL_BUILDS = new GameBuildMask().add_all();

    protected GameBuildDependentStorageStructure()
    {
        this(ALL_BUILDS);
    }

    protected GameBuildDependentStorageStructure(final GameBuildMask gamebuildMask)
    {
        this.gamebuildMask = gamebuildMask;
    }

    public boolean matchesGameBuild(final GameBuild build)
    {
        return gamebuildMask.contains(build);
    }

    protected abstract Class<? extends Annotation> getSpecificAnnotation();

    @Override
    public String toString()
    {
        final Field[] all_fields = ClassUtil.getAnnotatedDeclaredFields(getClass(),
                getSpecificAnnotation(), true);

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
