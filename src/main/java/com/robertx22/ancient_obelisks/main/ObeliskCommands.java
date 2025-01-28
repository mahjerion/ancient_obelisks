package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.ancient_obelisks.structure.ObeliskWorldData;
import com.robertx22.library_of_exile.command_wrapper.CommandBuilder;
import com.robertx22.library_of_exile.command_wrapper.PermWrapper;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;

public class ObeliskCommands {


    public static void init() {
        ApiForgeEvents.registerForgeEvent(RegisterCommandsEvent.class, event -> {
            var dis = event.getDispatcher();

            CommandBuilder.of(ObelisksMain.MODID, dis, x -> {

                x.addLiteral("wipe_world_data", PermWrapper.OP);

                x.action(e -> {
                    var world = e.getSource().getLevel();

                    ObeliskMapCapability.get(world).data = new ObeliskWorldData();

                    e.getSource().getPlayer().sendSystemMessage(Component.literal(
                            "Obelisk world data wiped, you should only do this when wiping the dimension's folder too! The dimension folder is in: savefolder\\dimensions\\ancient_obelisks").withStyle(ChatFormatting.GREEN));
                });

            }, "Applies an item modification to the item in player's hand.");
        });
    }


}
