package com.robertx22.ancient_obelisks.database;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.util.concurrent.CompletableFuture;

public class ObeliskDataGen implements DataProvider {

    public ObeliskDataGen() {

    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {

        ObeliskDatabase.OBELISK_TYPE.getDatapackGenerator().run(pOutput);
        ObeliskDatabase.ATTRIBUTE_AFFIX.getDatapackGenerator().run(pOutput);

        //DataProvider.saveStable(pOutput, x.serializeRecipe(), target);

        return CompletableFuture.completedFuture(null); // todo this is bad, but would it work?
        // i think this is only needed if you dont directly save the jsons yourself?
    }


    @Override
    public String getName() {
        return "obelisk_data";
    }
}
