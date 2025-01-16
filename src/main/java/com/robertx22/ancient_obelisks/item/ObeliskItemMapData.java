package com.robertx22.ancient_obelisks.item;

import com.robertx22.ancient_obelisks.affix.AffixType;
import com.robertx22.ancient_obelisks.affix.ObeliskAffixData;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ObeliskItemMapData {

    public List<ObeliskAffixData> affixes = new ArrayList<>();

    public int x = 0;
    public int z = 0;

    public ChunkPos getOrSetStartPos(Level world, ItemStack stack) {

        if (x == 0 && z == 0) {
            var start = ObeliskMapCapability.get(world).data.counter.getNextAndIncrement();
            x = start.x;
            z = start.z;
        }
        ObeliskItemNbt.OBELISK_MAP.saveTo(stack, this);
        return new ChunkPos(x, z);
    }

    public void addAffix() {

        int lastWave = affixes.stream().mapToInt(x -> x.unlocksAtWave).max().orElse(0);

        var type = RandomUtils.randomFromList(AffixType.ObeliskAffixes());
        var affix = type.getDatabase().random();

        int wave = lastWave + 1;

        var data = new ObeliskAffixData(affix.id, wave, type);

        affixes.add(data);
    }

    // todo affixes
}
