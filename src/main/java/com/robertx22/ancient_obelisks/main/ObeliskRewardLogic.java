package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.structure.ObeliskMapData;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ObeliskRewardLogic {

    public static int whileRoll(float chance) {
        int amount = 0;

        while (chance > 0) {

            float maxChance = 100;

            float currentChance = chance;

            if (currentChance > maxChance) {
                currentChance = maxChance;
            }

            chance -= currentChance;

            if (RandomUtils.roll(currentChance)) {
                amount++;
            }

        }
        return amount;

    }

    public static void spawnChests(ObeliskMapData d, Level world, BlockPos pos) {

        for (Player p : ObelisksMain.OBELISK_MAP_STRUCTURE.getAllPlayersInMap(world, pos)) {
            p.sendSystemMessage(ObeliskWords.OBELISK_END.get().withStyle(ChatFormatting.DARK_PURPLE));
        }

        int chests = whileRoll(d.getTotalRewardChance());

        if (chests > ObeliskConfig.get().MAX_CHEST_REWARDS.get()) {
            chests = ObeliskConfig.get().MAX_CHEST_REWARDS.get();
        }
        if (chests < 1) {
            chests = 1; // compensation prize lol
        }

        for (int i = 0; i < chests; i++) {
            var cpos = findNearbyFreeChestPos(world, pos);
            if (cpos != null) {
                spawnChest(world, cpos, ObeliskLootTables.LOOT);
            }
        }
    }

    public static void spawnChest(Level world, BlockPos pos, ResourceLocation loottable) {

        world.setBlock(pos, Blocks.CHEST.defaultBlockState(), Block.UPDATE_ALL);

        if (world.getBlockEntity(pos) instanceof ChestBlockEntity ce) {
            ce.setLootTable(loottable, world.random.nextLong());
        }

    }

    public static BlockPos findNearbyFreeChestPos(Level world, BlockPos pos) {
        BlockPos nearest = null;
        int rad = 5;
        for (int x = -rad; x < rad; x++) {
            for (int y = -2; y < 2; y++) {
                for (int z = -rad; z < rad; z++) {

                    var check = pos.offset(x, y, z);
                    var state = world.getBlockState(check);

                    if (!state.isAir() && !state.is(Blocks.CHEST) && !state.is(ObeliskEntries.OBELISK_REWARD_BLOCK.get())) {
                        if (world.getBlockState(check.above()).isAir()) {
                            if (nearest == null || nearest.distSqr(pos) > check.above().distSqr(pos)) {
                                nearest = check.above();
                            }
                        }
                    }
                }
            }
        }
        return nearest;
    }

    public static void init() {

        ApiForgeEvents.registerForgeEvent(LivingDeathEvent.class, x -> {
            LivingEntity en = x.getEntity();
            var world = en.level();
            if (!world.isClientSide) {
                ObelisksMain.ifMapData(world, en.blockPosition()).ifPresent(d -> {
                    d.mob_kills++;
                });
            }
        });

    }
}
