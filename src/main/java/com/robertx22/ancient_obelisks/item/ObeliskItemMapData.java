package com.robertx22.ancient_obelisks.item;

import com.robertx22.ancient_obelisks.main.ObeliskMobTierStats;
import com.robertx22.ancient_obelisks.main.ObeliskWords;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.affix.types.AttributeMobAffix;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObeliskItemMapData {

    public HashMap<Integer, List<ExileAffixData>> affixes = new HashMap<>();

    public int tier = 0;

    public int x = 0;
    public int z = 0;

    public int maxWaves = 0;


    public List<Component> getTierTooltip() {
        List<Component> all = new ArrayList<>();

        all.add(ObeliskWords.OBELISK_TIER_X.get(tier).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        all.add(AttributeMobAffix.getTooltip(Attributes.MAX_HEALTH, ObeliskMobTierStats.hpMod(tier)));
        all.add(AttributeMobAffix.getTooltip(Attributes.ATTACK_DAMAGE, ObeliskMobTierStats.dmgMod(tier)));

        return all;

    }

    public int totalAffixes() {
        int i = 0;
        for (Map.Entry<Integer, List<ExileAffixData>> en : affixes.entrySet()) {
            i += en.getValue().size();
        }
        return i;
    }

    public List<Component> getAffixesTooltip() {

        List<Component> all = new ArrayList<>();


        for (int i = 0; i < maxWaves; i++) {

            all.add(ObeliskWords.ITEM_WAVE_X.get(i + 1).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));

            for (ExileAffixData data : getAffixesForWave(i)) {
                var prefix = Component.literal("[").append(
                                data.getAffix().affects.getTranslation(TranslationType.NAME).getTranslatedName())
                        .append("] - ");
                prefix.withStyle(data.getAffix().affects.color);

                var text = prefix.append(data.getAffix().getParamName(data.perc));

                all.add(text);
            }

        }
        return all;
    }

    public List<ExileAffixData> getAffixesForWave(int wave) {
        return affixes.getOrDefault(wave, new ArrayList<>());
    }

    public void generateInitialWaves() {

        for (int i = 0; i < 3; i++) {
            maxWaves++;
            addRandomAffix();
        }
    }

    public ChunkPos getOrSetStartPos(Level world, ItemStack stack) {

        if (x == 0 && z == 0) {
            var start = ObeliskMapCapability.get(world).data.counter.getNextAndIncrement();
            x = start.x;
            z = start.z;
        }
        ObeliskItemNbt.OBELISK_MAP.saveTo(stack, this);
        return new ChunkPos(x, z);
    }

    public int getWaveWithLeastAffixes() {
        int least = 100;
        int index = -1;

        for (int i = 0; i < maxWaves; i++) {
            int amountCheck = affixes.getOrDefault(i, new ArrayList<>()).size();
            if (amountCheck < least) {
                least = amountCheck;
                index = i;
            }
        }
        return index;
    }

    public void addRandomAffix() {

        List<String> allCurrent = new ArrayList<>();
        for (List<ExileAffixData> value : affixes.values()) {
            for (ExileAffixData d : value) {
                allCurrent.add(d.affix);
            }
        }
        // todo might want to mark only some as obelisk, or obelisk exclusive?
        var affix = LibDatabase.MobAffixes().getFilterWrapped(x -> {
            if (allCurrent.contains(x.GUID())) {
                return false;
            }
            return true;
        }).random();

        var data = new ExileAffixData(affix.id, RandomUtils.RandomRange(0, 100));

        // todo make sure u cant get same affix twice!

        int wave = getWaveWithLeastAffixes();

        if (wave > -1) {
            addAffixToWave(wave, data);
        }

    }

    public void addAffixToWave(int wave, ExileAffixData data) {

        if (!affixes.containsKey(wave)) {
            affixes.put(wave, new ArrayList<>());
        }
        affixes.get(wave).add(data);
    }

}
