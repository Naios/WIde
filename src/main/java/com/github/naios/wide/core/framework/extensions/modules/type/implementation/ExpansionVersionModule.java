
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

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
