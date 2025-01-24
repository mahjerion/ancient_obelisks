package com.robertx22.ancient_obelisks.structure;

import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.item.ObeliskItemMapData;
import com.robertx22.ancient_obelisks.main.ObeliskWords;
import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.database.mob_list.MobList;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ObeliskMapData {
    // todo

    public ObeliskItemMapData item = new ObeliskItemMapData();

    public int currentWave = 0;
    public int mobsLeftForWave = 0;
    public int mob_kills = 0;

    public int endsIn = 30;

    // as tick is per each obelisk block, this is crappy
    public int waveCd = 0;

    public int x = 0;
    public int z = 0;

    public boolean canSpawnRewards = false;
    public boolean spawnedRewards = false;


    public float getTotalRewardChance() {
        float chance = mob_kills * ObeliskConfig.get().LOOT_CHANCE_PER_MOB_KILL.get().floatValue();
        chance *= (1 + item.tier * ObeliskConfig.get().LOOT_MULTI_PER_TIER.get().floatValue());
        chance *= (1 + item.totalAffixes() * ObeliskConfig.get().LOOT_MULTI_PER_AFFIX.get().floatValue());
        return chance;
    }

    public void tryStartNewWave(Level world, BlockPos pos) {
        if (waveCd-- > 0) {
            return;
        }

        waveCd = 100; // todo
        currentWave++;
        mobsLeftForWave = 50; // todo

        for (Player p : ObelisksMain.OBELISK_MAP_STRUCTURE.getAllPlayersInMap(world, pos)) {
            if (currentWave == item.maxWaves) {
                p.sendSystemMessage(ObeliskWords.LAST_WAVE.get(currentWave).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            } else {
                p.sendSystemMessage(ObeliskWords.WAVE_X_STARTING.get(currentWave).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
        }
    }


    public void waveLogicSecond(Level world, BlockPos pos) {
        if (currentWave >= item.maxWaves) {
            if (endsIn-- < 1) {
                canSpawnRewards = true;
            }
            return;
        }

        if (mobsLeftForWave < 1) {
            tryStartNewWave(world, pos);
        }


        if (mobsLeftForWave > 0) {

            int toSpawn = 1 + (RandomUtils.RandomRange(1, currentWave));
            if (toSpawn > mobsLeftForWave) {
                toSpawn = mobsLeftForWave;
            }

            var mobs = MobList.PREDETERMINED.getPredeterminedRandom(world, pos);

            for (int i = 0; i < toSpawn; i++) {
                var mob = RandomUtils.weightedRandom(mobs.mobs);
                spawnMob(world, pos, mob.getType());
            }
        }

    }

    public void spawnMob(Level world, BlockPos pos, EntityType type) {

        this.mobsLeftForWave--;

        LivingEntity en = (LivingEntity) type.create(world);
        en.setPos(pos.getX(), pos.getY(), pos.getZ());

        if (en instanceof Mob mob) {
            mob.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, (SpawnGroupData) null, (CompoundTag) null);
        }

        world.addFreshEntity(en);
    }
}
