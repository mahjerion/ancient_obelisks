package com.robertx22.ancient_obelisks.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ObeliskConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ObeliskConfig CONFIG;

    static {
        final Pair<ObeliskConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ObeliskConfig::new);
        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    // todo these could be reused for other mods maybe
    public ForgeConfigSpec.IntValue MAX_OBELISK_TIER;
    public ForgeConfigSpec.DoubleValue MOB_HP_PER_TIER;
    public ForgeConfigSpec.DoubleValue MOB_DMG_PER_TIER;


    public static ObeliskConfig get() {
        return CONFIG;
    }


    ObeliskConfig(ForgeConfigSpec.Builder b) {
        b.comment("Ancient Obelisk Configs")
                .push("general");

        MAX_OBELISK_TIER = b
                .comment("Each obelisk tier increases the Mob Stats and rewards")
                .defineInRange("MAX_OBELISK_TIER", 10, 1, 1000);

        MOB_HP_PER_TIER = b
                .comment("Mob hp multiplier per obelisk tier")
                .defineInRange("MOB_HP_PER_TIER", 0.2F, 0, 1000);

        MOB_DMG_PER_TIER = b
                .comment("Mob attack damage multiplier per obelisk tier")
                .defineInRange("MOB_DMG_PER_TIER", 0.05F, 0, 1000);


        b.pop();
    }


}
