package wide.core.framework.game;

public enum GameBuild
{
    V3_3_0_10958("3.3.0", Expansion.WRATH_OF_THE_LICH_KING),
    V3_3_5a_12340("3.3.5a", Expansion.WRATH_OF_THE_LICH_KING),
    V4_0_3_13329("4.0.3", Expansion.CATACLYSM),
    V4_0_6_13596("4.0.6", Expansion.CATACLYSM),
    V4_1_0_13914("4.1", Expansion.CATACLYSM),
    V4_2_0_14480("4.2.0", Expansion.CATACLYSM),
    V4_2_2_14545("4.2.2", Expansion.CATACLYSM),
    V4_3_0_15005("4.3.0", Expansion.CATACLYSM),
    V4_3_2_15211("4.3.2", Expansion.CATACLYSM),
    V4_3_3_15354("4.3.3", Expansion.CATACLYSM),
    V4_3_4_15595("4.3.4", Expansion.CATACLYSM),
    V5_0_4_16016("5.0.4", Expansion.MISTS_OF_PANDARIA),
    V5_0_5_16048("5.0.5", Expansion.MISTS_OF_PANDARIA),
    V5_1_0_16309("5.1.0", Expansion.MISTS_OF_PANDARIA),
    V5_2_0_16650("5.2.0", Expansion.MISTS_OF_PANDARIA),
    V5_3_0_16981("5.3.0", Expansion.MISTS_OF_PANDARIA),
    V5_4_0_17359("5.4.0", Expansion.MISTS_OF_PANDARIA),
    V5_4_1_17538("5.4.1", Expansion.MISTS_OF_PANDARIA),
    V5_4_2_17658("5.4.2", Expansion.MISTS_OF_PANDARIA),
    V5_4_7_17898("5.4.7", Expansion.MISTS_OF_PANDARIA),
    V5_4_8_18291("5.4.8", Expansion.MISTS_OF_PANDARIA),
    V6_0_2_19033("6.0.2", Expansion.WARLORDS_OF_DRAENOR),
    V6_0_3_19103("6.0.3", Expansion.WARLORDS_OF_DRAENOR);

    private final String version;
    private final Expansion expansion;

    private GameBuild(String version, Expansion expansion)
    {
        this.version = version;
        this.expansion = expansion;
    }

    public final String getVersion()
    {
        return version;
    }

    public Expansion getExpansion()
    {
        return expansion;
    }

    @Override
    public String toString()
    {
        return version;
    }
}
