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


    public static ObeliskConfig get() {
        return CONFIG;
    }


    ObeliskConfig(ForgeConfigSpec.Builder b) {
        b.comment("Ancient Obelisk Configs")
                .push("general");


        b.pop();
    }


}
