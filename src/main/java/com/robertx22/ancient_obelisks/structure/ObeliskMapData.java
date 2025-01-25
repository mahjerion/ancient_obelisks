package com.robertx22.ancient_obelisks.structure;

import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.item.ObeliskItemMapData;
import com.robertx22.ancient_obelisks.main.ObeliskWords;
import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.mob_list.MobList;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class ObeliskMapData {
    // todo

    public ObeliskItemMapData item = new ObeliskItemMapData();

    public int currentWave = -1;
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
        chance *= item.getLootMulti();

        return chance;
    }

    public void tryStartNewWave(Level world, BlockPos pos) {
        if (waveCd-- > 0) {
            return;
        }

        waveCd = ObeliskConfig.get().WAVE_COOLDOWN_SECONDS.get();
        currentWave++;
        mobsLeftForWave = ObeliskConfig.get().TOTAL_MOBS_PER_WAVE.get();

        for (Player p : ObelisksMain.OBELISK_MAP_STRUCTURE.getAllPlayersInMap(world, pos)) {
            if (currentWave == (item.maxWaves - 1)) {
                p.sendSystemMessage(ObeliskWords.LAST_WAVE.get(currentWave).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            } else {
                p.sendSystemMessage(ObeliskWords.WAVE_X_STARTING.get(currentWave + 1).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
            for (ExileAffixData affix : this.item.getAffixesForWave(currentWave)) {
                p.sendSystemMessage(ObeliskWords.NEW_WAVE_AFFIX.get(affix.getAffix().getPrefixedName(affix.perc)).withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }


    public void waveLogicSecond(Level world, BlockPos pos) {
        if (currentWave >= (item.maxWaves - 1)) {
            if (endsIn-- < 1) {
                canSpawnRewards = true;
            }
            return;
        }

        if (mobsLeftForWave < 1) {
            tryStartNewWave(world, pos);
        }


        if (mobsLeftForWave > 0) {


            // todo on configs with 100% spawn chance this is useless
            float spawnChance = ObeliskConfig.get().MOB_SPAWN_CHANCE.get() * item.getSpawnRateMulti();
        
            if (RandomUtils.roll(spawnChance)) {
                int toSpawn = ObeliskConfig.get().MOB_SPAWNS_PER_SECOND.get();
                if (toSpawn > mobsLeftForWave) {
                    toSpawn = mobsLeftForWave;
                }


                var mobs = MobList.PREDETERMINED.getPredeterminedRandom(world, pos);

                List<Direction> dirs = Arrays.asList(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);

                for (int i = 0; i < toSpawn; i++) {
                    var mob = RandomUtils.weightedRandom(mobs.mobs);
                    spawnMob(world, pos.offset(RandomUtils.randomFromList(dirs).getNormal().multiply(2)), mob.getType());
                }
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
