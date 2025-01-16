package com.robertx22.ancient_obelisks.block_entity;

import com.robertx22.ancient_obelisks.main.CommonInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObeliskBE extends BlockEntity {


    public ObeliskBE(BlockPos pPos, BlockState pBlockState) {
        super(CommonInit.OBELISK_BE.get(), pPos, pBlockState);
    }
}
