package com.robertx22.ancient_obelisks.block;

import com.robertx22.ancient_obelisks.block_entity.ObeliskBE;
import com.robertx22.ancient_obelisks.item.ObeliskItemMapData;
import com.robertx22.ancient_obelisks.item.ObeliskItemNbt;
import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.ancient_obelisks.structure.ObeliskMapData;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.database.relic.stat.RelicStatsContainer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.utils.TeleportUtils;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObeliskBlock extends BaseEntityBlock {
    public ObeliskBlock() {
        super(BlockBehaviour.Properties.of().strength(10).noOcclusion().lightLevel(x -> 10));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {

        List<ItemStack> all = new ArrayList<>();
        BlockEntity blockentity = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockentity instanceof ObeliskBE be) {
            all.add(asItem().getDefaultInstance());

            for (int i = 0; i < be.deviceInv.getContainerSize(); i++) {
                var s = be.deviceInv.getItem(i);
                if (!s.isEmpty()) {
                    all.add(s.copy());
                }
            }
        }

        return all;
    }

    /**
     * @param relics resolved once, right before the instance data is written. The device GUI consumes relic
     *               uses inside this supplier. May yield null when no relics are slotted.
     */
    public static void startNewMap(Player p, ItemStack stack, ObeliskBE be, Supplier<RelicStatsContainer> relics) {

        ObeliskItemMapData map = ObeliskItemNbt.OBELISK_MAP.loadFrom(stack);

        var count = map.getOrSetStartPos(p.level(), stack);
        var start = ObelisksMain.OBELISK_MAP_STRUCTURE.getStartFromCounter(count.x, count.z);
        var pos = TeleportUtils.getSpawnTeleportPos(ObelisksMain.OBELISK_MAP_STRUCTURE, start.getMiddleBlockPosition(5));

        var pdata = PlayerDataCapability.get(p);

        var data = new ObeliskMapData();
        data.item = map;
        data.x = start.x;
        data.z = start.z;

        // kept on the obelisk's own data rather than in LibMapCap: that store is keyed by grid position
        // with no dimension, so an obelisk instance would overwrite the dungeon instance on the same key.
        // ObelisksMain's GRAB_LIB_MAP_DATA listener hands these back to LibMapCap.getData.
        RelicStatsContainer relicStats = relics == null ? null : relics.get();
        if (relicStats != null) {
            data.relicStats = relicStats;
            data.hasRelics = true;
        }

        be.x = count.x;
        be.z = count.z;

        be.currentWorldUUID = ObeliskMapCapability.get(p.level()).data.data.uuid;

        be.setChanged();

        // the stack is the one in the device's map slot (or the free in-map blank), so this empties the slot
        stack.shrink(1);
        be.deviceInv.setChanged();

        ObeliskMapCapability.get(p.level()).data.data.setData(p, data, ObelisksMain.OBELISK_MAP_STRUCTURE, start.getMiddleBlockPosition(5));

        // the instance is created here, so this is the one entry that gets the spawn grace
        pdata.mapTeleports.entranceTeleportLogic(p, ObelisksMain.DIMENSION_KEY, pos, true);

    }

    public static void joinCurrentMap(Player p, ObeliskBE be) {

        var start = ObelisksMain.OBELISK_MAP_STRUCTURE.getStartFromCounter(be.x, be.z);
        var pos = TeleportUtils.getSpawnTeleportPos(ObelisksMain.OBELISK_MAP_STRUCTURE, start.getMiddleBlockPosition(5));
        var pdata = PlayerDataCapability.get(p);
        // no grace: this rejoins an instance that is already running, whether that's the owner coming
        // back or someone joining their run. its mobs already exist, so the grace would protect nobody
        // and anyInGrace would stall the waves for everyone in the arena.
        pdata.mapTeleports.entranceTeleportLogic(p, ObelisksMain.DIMENSION_KEY, pos, false);
    }


    @Override
    public InteractionResult use(BlockState pState, Level world, BlockPos pPos, Player p, InteractionHand pHand, BlockHitResult pHit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        var be = world.getBlockEntity(pPos);
        if (!(be instanceof ObeliskBE)) {
            ObelisksMain.debugMsg(p, "Missing Block entity");
            return InteractionResult.SUCCESS;
        }

        // slotting the map and relics, starting and joining all go through the shared device GUI, which
        // the main mod opens for this player
        ExileEvents.OPEN_MAP_DEVICE.callEvents(new ExileEvents.OpenMapDeviceEvent(p, world, pPos));
        return InteractionResult.SUCCESS;
    }


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
