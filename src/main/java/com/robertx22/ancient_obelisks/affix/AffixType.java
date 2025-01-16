package com.robertx22.ancient_obelisks.affix;

import com.robertx22.ancient_obelisks.affix.types.BaseObeliskAffix;
import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;

import java.util.Arrays;
import java.util.List;

// todo maybe this should go to library so affixes can be used by other map mobs?
public enum AffixType {
    ATTRIBUTE() {
        @Override
        public ExileRegistryContainer<? extends BaseObeliskAffix> getDatabase() {
            return ObeliskDatabase.getAttributeAffix();
        }
    };


    public abstract ExileRegistryContainer<? extends BaseObeliskAffix> getDatabase();


    public static List<AffixType> ObeliskAffixes() {
        return Arrays.asList(ATTRIBUTE);
    }
}
