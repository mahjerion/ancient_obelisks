package com.robertx22.ancient_obelisks.block_entity;

import com.robertx22.ancient_obelisks.main.CommonInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObeliskMobSpawnerBE extends BlockEntity {


    public ObeliskMobSpawnerBE(BlockPos pPos, BlockState pBlockState) {
        super(CommonInit.SPAWNER_BE.get(), pPos, pBlockState);
    }
}
