package com.robertx22.ancient_obelisks.block_entity;

import com.robertx22.ancient_obelisks.main.ObeliskEntries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObeliskBE extends BlockEntity {


    public boolean gaveMap = false;
    public int x = -1;
    public int z = -1;

    public boolean isActivated() {
        return x != -1 || z != -1;
    }

    public void setGaveMap() {
        this.gaveMap = true;
        this.setChanged();
    }

    public ObeliskBE(BlockPos pPos, BlockState pBlockState) {
        super(ObeliskEntries.OBELISK_BE.get(), pPos, pBlockState);

    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("gave", gaveMap);
        nbt.putInt("xp", x);
        nbt.putInt("zp", z);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.gaveMap = pTag.getBoolean("gave");
        this.x = pTag.getInt("xp");
        this.z = pTag.getInt("zp");
    }

}
