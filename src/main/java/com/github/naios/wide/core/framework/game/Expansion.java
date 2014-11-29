package com.github.naios.wide.core.framework.game;

public enum Expansion
{
    CLASSIC("CL"),
    BURNING_CRUSADE("BC"),
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
