
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.entities.game;

public enum Expansion
{
    CLASSIC("CL"),
    THE_BURNING_CRUSADE("TBC"),
    WRATH_OF_THE_LICH_KING("WOTLK"),
    CATACLYSM("CATA"),
    MISTS_OF_PANDARIA("MOP"),
    WARLORDS_OF_DRAENOR("WOD");

    private final String shortVersion;

    private Expansion(final String shortVersion)
    {
        this.shortVersion = shortVersion;
    }

    public String getShortVersion()
    {
        return shortVersion;
    }

    public int getMajorVersion()
    {
        return ordinal();
    }
}
