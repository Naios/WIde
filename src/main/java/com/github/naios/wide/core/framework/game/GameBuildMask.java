package com.github.naios.wide.core.framework.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameBuildMask
{
    private final Set<GameBuild> container = new HashSet<GameBuild>();

    public GameBuildMask()
    {
    }

    public GameBuildMask(final GameBuild... builds)
    {
        add(builds);
    }

    public GameBuildMask add(final GameBuild build)
    {
        container.add(build);
        return this;
    }

    public GameBuildMask add(final GameBuild... builds)
    {
        container.addAll(Arrays.asList(builds));
        return this;
    }

    public GameBuildMask add_range(final GameBuild begin, final GameBuild end)
    {
        return add(Arrays.copyOfRange(GameBuild.values(), begin.ordinal(), end.ordinal()));
    }

    public GameBuildMask add_expansion(final Expansion... expansions)
    {
        for (final Expansion expansion : expansions)
            for (final GameBuild build : GameBuild.values())
                if (build.getExpansion().equals(expansion))
                    add(build);

        return this;
    }

    public GameBuildMask add_all()
    {
        return add(GameBuild.values());
    }

    public GameBuildMask remove(final GameBuild build)
    {
        container.remove(build);
        return this;
    }

    public GameBuildMask remove(final GameBuild... build)
    {
        container.removeAll(Arrays.asList(build));
        return this;
    }

    public GameBuildMask remove_range(final GameBuild begin, final GameBuild end)
    {
        return remove(Arrays.copyOfRange(GameBuild.values(), begin.ordinal(), end.ordinal()));
    }

    public GameBuildMask remove_expansion(final Expansion... expansions)
    {
        for (final Expansion expansion : expansions)
            for (final GameBuild build : GameBuild.values())
                if (build.getExpansion().equals(expansion))
                    remove(build);

        return this;
    }

    public GameBuildMask remove_all()
    {
        container.clear();
        return this;
    }

    /**
     * is the most used method to check a GameBuildMask against a gamebuild
     *
     * @param gamebuild you want to check
     * @return if the build is included in the mask
     */
    public boolean contains(final GameBuild build)
    {
        return container.contains(build);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GameBuildMask other = (GameBuildMask) obj;
        if (container == null)
        {
            if (other.container != null)
                return false;
        }
        else if (!container.equals(other.container))
            return false;
        return true;
    }
}
