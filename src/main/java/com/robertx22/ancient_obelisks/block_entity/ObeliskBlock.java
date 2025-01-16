package com.robertx22.ancient_obelisks.block_entity;

import com.robertx22.ancient_obelisks.item.ObeliskItemMapData;
import com.robertx22.ancient_obelisks.item.ObeliskItemNbt;
import com.robertx22.ancient_obelisks.item.ObeliskMapItem;
import com.robertx22.ancient_obelisks.main.CommonInit;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ObeliskBlock extends BaseEntityBlock {
    public ObeliskBlock() {
        super(BlockBehaviour.Properties.of().strength(10).noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState pState, Level world, BlockPos pPos, Player p, InteractionHand pHand, BlockHitResult pHit) {

        if (!world.isClientSide) {
            ItemStack stack = p.getMainHandItem();

            if (ObeliskItemNbt.OBELISK_MAP.has(stack)) {

                ObeliskItemMapData map = ObeliskItemNbt.OBELISK_MAP.loadFrom(stack);

                var count = map.getOrSetStartPos(world, stack);
                var start = CommonInit.OBELISK_MAP_STRUCTURE.getStartFromCounter(count.x, count.z);
                var pos = CommonInit.OBELISK_MAP_STRUCTURE.getSpawnTeleportPos(start.getMiddleBlockPosition(5));

                var pdata = PlayerDataCapability.get(p);

                pdata.mapTeleports.entranceTeleportLogic(p, CommonInit.DIMENSION_KEY, pos);
            } else {

                var be = world.getBlockEntity(pPos);

                if (be instanceof ObeliskBE obe) {

                    if (!obe.gaveMap) {
                        obe.setGaveMap();
                        var map = ObeliskMapItem.blankMap();
                        giveItem(map, p);
                    }

                }

            }
        }

        return InteractionResult.SUCCESS;
    }

    public static void giveItem(ItemStack stack, Player player) {
        if (player.addItem(stack) == false) {
            player.spawnAtLocation(stack, 1F);
        }
        player.getInventory().setChanged();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ObeliskBE(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return new BlockEntityTicker<T>() {
            @Override
            public void tick(Level pLevel, BlockPos pPos, BlockState pState, T pBlockEntity) {
                // todo
            }
        };
    }

}
