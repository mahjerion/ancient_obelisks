package com.robertx22.ancient_obelisks.main;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ObeliskLootTables {

    public static ResourceLocation DEFAULT = ObelisksMain.id("obelisk_loot");

    public static class ObeliskLootTableProvider implements LootTableSubProvider {

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            var table = LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3, 6))
                            .add(LootItem.lootTableItem(ObeliskEntries.ENVY.get()))
                            .add(LootItem.lootTableItem(ObeliskEntries.WRATH.get()))
                            .add(LootItem.lootTableItem(ObeliskEntries.GREED.get()))
                    );

            output.accept(DEFAULT, table);
        }
    }

}
