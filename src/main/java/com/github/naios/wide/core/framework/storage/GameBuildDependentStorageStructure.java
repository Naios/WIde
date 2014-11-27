package com.github.naios.wide.core.framework.storage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.framework.game.GameBuild;
import com.github.naios.wide.core.framework.game.GameBuildMask;
import com.github.naios.wide.core.framework.util.ClassUtil;

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
                final Object object = field.get(this);
                if (object != null)
                    builder.append(object.toString());
                else
                    builder.append(object);
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
