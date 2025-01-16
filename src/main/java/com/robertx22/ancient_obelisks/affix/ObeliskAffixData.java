package com.robertx22.ancient_obelisks.affix;

import com.robertx22.ancient_obelisks.affix.types.BaseObeliskAffix;

public class ObeliskAffixData {

    public String id = "";
    public int unlocksAtWave = 0;
    public AffixType type;

    public ObeliskAffixData(String id, int unlocksAtWave, AffixType type) {
        this.id = id;
        this.unlocksAtWave = unlocksAtWave;
        this.type = type;
    }


    public BaseObeliskAffix getAffix() {
        return type.getDatabase().get(id);
    }
}
