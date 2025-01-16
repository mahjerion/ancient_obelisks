package com.robertx22.ancient_obelisks.item;

import com.robertx22.ancient_obelisks.affix.AffixType;
import com.robertx22.ancient_obelisks.affix.ObeliskAffixData;
import com.robertx22.library_of_exile.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class ObeliskItemMapData {

    public List<ObeliskAffixData> affixes = new ArrayList<>();

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
