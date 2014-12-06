package com.github.naios.wide.core.framework.extensions.modules.type.implementation;

import com.github.naios.wide.core.framework.game.Expansion;
import com.github.naios.wide.core.framework.game.GameBuildMask;

public class ExpansionVersionModule extends VersionModuleImplementation
{
    private final GameBuildMask gamebuilds;

    public ExpansionVersionModule(final Expansion expansion)
    {
        gamebuilds = new GameBuildMask().addExpansion(expansion);
    }

    @Override
    public GameBuildMask getGameBuilds()
    {
        return gamebuilds;
    }
}
