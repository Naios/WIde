
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.framework.internal.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameBuildMask
{
    private final Set<GameBuild> container = new HashSet<GameBuild>();

    public final static GameBuildMask ALL_BUILDS = new GameBuildMask().addAll();

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

    public GameBuildMask addUntil(final GameBuild until)
    {
        return addRange(GameBuild.values()[0], until);
    }

    public GameBuildMask addRange(final GameBuild begin, final GameBuild end)
    {
        return add(Arrays.copyOfRange(GameBuild.values(), begin.ordinal(), end.ordinal() + 1));
    }

    public GameBuildMask addExpansion(final Expansion... expansions)
    {
        for (final Expansion expansion : expansions)
            for (final GameBuild build : GameBuild.values())
                if (build.getExpansion().equals(expansion))
                    add(build);

        return this;
    }

    public GameBuildMask addAll()
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

    public GameBuildMask removeUntil(final GameBuild until)
    {
        return removeRange(GameBuild.values()[0], until);
    }

    public GameBuildMask removeRange(final GameBuild begin, final GameBuild end)
    {
        return remove(Arrays.copyOfRange(GameBuild.values(), begin.ordinal(), end.ordinal() + 1));
    }

    public GameBuildMask removeExpansion(final Expansion... expansions)
    {
        for (final Expansion expansion : expansions)
            for (final GameBuild build : GameBuild.values())
                if (build.getExpansion().equals(expansion))
                    remove(build);

        return this;
    }

    public GameBuildMask removeAll()
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
