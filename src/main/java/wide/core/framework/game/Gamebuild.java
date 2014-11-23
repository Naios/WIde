package wide.core.framework.game;

public enum Gamebuild
{
    V3_3_0_10958("3.3.0"),
    V3_3_5a_12340("3.3.5a"),
    V4_0_3_13329("4.0.3"),
    V4_0_6_13596("4.0.6"),
    V4_1_0_13914("4.1"),
    V4_2_0_14480("4.2.0"),
    V4_2_2_14545("4.2.2"),
    V4_3_0_15005("4.3.0"),
    V4_3_2_15211("4.3.2"),
    V4_3_3_15354("4.3.3"),
    V4_3_4_15595("4.3.4"),
    V5_0_4_16016("5.0.4"),
    V5_0_5_16048("5.0.5"),
    V5_1_0_16309("5.1.0"),
    V5_2_0_16650("5.2.0"),
    V5_3_0_16981("5.3.0"),
    V5_4_0_17359("5.4.0"),
    V5_4_1_17538("5.4.1"),
    V5_4_2_17658("5.4.2"),
    V5_4_7_17898("5.4.7"),
    V5_4_8_18291("5.4.8"),
    V6_0_2_19033("6.0.2"),
    V6_0_3_19103("6.0.3");

    private final String shortversion;

    private Gamebuild(String shortversion)
    {
        this.shortversion = shortversion;
    }

    @Override
    public String toString()
    {
        return shortversion;
    }

    public Expansion getExpansion()
    {
        // Returns the expansion basd on the first integer of the shortversion
        return Expansion.values()[Integer.valueOf(toString().charAt(0)) - 1];
    }
}
