package com.robertx22.ancient_obelisks.item;

import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

// todo i probably want a common item for all map mods?
public class HomeItem extends Item {

    public HomeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player p, InteractionHand pUsedHand) {

        if (!pLevel.isClientSide) {

            if (MapDimensions.isMap(p.level())) {
                if (p.isCrouching()) {
                    PlayerDataCapability.get(p).mapTeleports.teleportHome(p);
                } else {
                    PlayerDataCapability.get(p).mapTeleports.exitTeleportLogic(p);
                }
                return InteractionResultHolder.success(p.getItemInHand(pUsedHand));
            }


        }
        return InteractionResultHolder.pass(p.getItemInHand(pUsedHand));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(ObelisksMain.MODID + ".homeitem"));
    }
}
