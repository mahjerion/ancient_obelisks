package com.robertx22.ancient_obelisks.database.holders;

import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.database.relic.affix.RelicAffix;
import com.robertx22.library_of_exile.database.relic.stat.RelicMod;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class ObeliskRelicAffixes extends ExileKeyHolder<MapDataBlock> {

    public static ObeliskRelicAffixes INSTANCE = new ObeliskRelicAffixes(ObelisksMain.REGISTER_INFO);

    public ObeliskRelicAffixes(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    static String TYPE = ObelisksMain.MODID;

    // the dungeon_realm relic type (its id is the modid; this addon doesn't depend on dungeon_realm).
    // Only dungeon relics enter the map device, so an "obelisk inside this map" guarantee belongs on them,
    // not on obelisk relics, which only go into the obelisk altar.
    static String IMPLICIT_TYPE = "dungeon_realm";

    public ExileKey<RelicAffix, KeyInfo> BONUS_OBELISK_CHANCE = ExileKey.ofId(this, "bonus_obelisk_chance", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.BONUS_OBELISK_CHANCE, 3, 25));
    });
    public ExileKey<RelicAffix, KeyInfo> TRIPLE_OBELISK_CHANCE = ExileKey.ofId(this, "triple_obelisk_chance", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.TRIPLE_OBELISK_CHANCE, 1, 5));
    });
    public ExileKey<RelicAffix, KeyInfo> TOTAL_WAVE_MOBS = ExileKey.ofId(this, "total_wave_mobs", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.TOTAL_WAVE_MOBS, 5, 25));
    });
    public ExileKey<RelicAffix, KeyInfo> MOB_SPAWN_CHANCE = ExileKey.ofId(this, "mob_spawn_chance", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.MOB_SPAWN_CHANCE, 2, 10));
    });
    public ExileKey<RelicAffix, KeyInfo> TRIPLE_CHEST_REWARD_CHANCE = ExileKey.ofId(this, "triple_chest_chance", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.TRIPLE_CHEST_REWARD_CHANCE, 1, 5));
    });

    public ExileKey<RelicAffix, KeyInfo> OBELISK_CONTENT = ExileKey.ofId(this, "obelisk_content", x -> {
        return new RelicAffix(x.GUID(), TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.OBELISK_CONTENT, 25, 100));
    });

    // Implicit, rolled into a dungeon relic's dedicated implicit slot. Flat 100 so the guarantee doesn't
    // depend on the affix roll.
    public ExileKey<RelicAffix, KeyInfo> GUARANTEE_OBELISK_CONTENT = ExileKey.ofId(this, "guarantee_obelisk_content", x -> {
        return new RelicAffix(x.GUID(), IMPLICIT_TYPE, new RelicMod(ObeliskRelicStats.INSTANCE.GUARANTEE_OBELISK_CONTENT, 100, 100)).setImplicit();
    });

    @Override
    public void loadClass() {

    }
}
