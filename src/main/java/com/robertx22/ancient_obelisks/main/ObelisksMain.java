package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.ancient_obelisks.structure.ObeliskMapStructure;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfigDefaults;
import com.robertx22.library_of_exile.dimension.MapChunkGenEvent;
import com.robertx22.library_of_exile.dimension.MapChunkGens;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

@Mod("ancient_obelisks")
public class ObelisksMain {
    public static boolean RUN_DEV_TOOLS = true;

    public static String MODID = "ancient_obelisks";
    public static String DIMENSION_ID = "ancient_obelisks:obelisk";
    public static ResourceLocation DIMENSION_KEY = new ResourceLocation(DIMENSION_ID);

    public static ModRequiredRegisterInfo REGISTER_INFO = new ModRequiredRegisterInfo(MODID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }


    // other
    public static ObeliskMapStructure OBELISK_MAP_STRUCTURE = new ObeliskMapStructure();

    public static MapDimensionConfig getConfig() {
        return MapDimensionConfig.get(DIMENSION_KEY);
    }


    public ObelisksMain() {


        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
        });
        
        MapDimensionConfig.register(DIMENSION_KEY, new MapDimensionConfigDefaults(2));

        new ObeliskModConstructor(MODID, bus);

        if (RUN_DEV_TOOLS) {
            ExileRegistryUtil.setCurrentRegistarMod(ObelisksMain.MODID);

            ApiForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event -> {
                ObeliskDatabase.generateJsons();
            });
        }

        MapDimensions.register(new MapDimensionInfo(DIMENSION_KEY, OBELISK_MAP_STRUCTURE));

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ObeliskConfig.SPEC);


        bus.addListener(this::commonSetupEvent);

        // todo, separate this into a new mod, and rework some of these to not require mapdatas
        MapChunkGens.registerMapChunkGenerator(id("obelisk_chunk_gen"), new EventConsumer<MapChunkGenEvent>() {
            @Override
            public void accept(MapChunkGenEvent event) {
                if (event.mapId.equals("obelisk")) {
                    OBELISK_MAP_STRUCTURE.generateInChunk(event.world, event.manager, event.chunk.getPos());
                }
            }
        });


        ObeliskEntries.CREATIVE_TAB.register("ancient_obelisk_tab", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 2)
                .icon(() -> ObeliskEntries.OBELISK_ITEM.get().getDefaultInstance())
                .title(Component.translatable("ancient_obelisks.name").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                .displayItems(new CreativeModeTab.DisplayItemsGenerator() {
                    @Override
                    public void accept(CreativeModeTab.ItemDisplayParameters param, CreativeModeTab.Output output) {
                        output.accept(ObeliskEntries.OBELISK_ITEM.get());
                        output.accept(ObeliskEntries.OBELISK_MAP_ITEM.get());
                        output.accept(ObeliskEntries.HOME_TP_BACK.get());
                    }
                })
                .build());


        ObeliskDatabase.initRegistries();


        System.out.println("Ancient Obelisks loaded.");
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ObeliskClient.init();
    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {


        ComponentInit.reg();

        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, (Consumer<AttachCapabilitiesEvent<Level>>) x -> {
            x.addCapability(ObeliskMapCapability.RESOURCE, new ObeliskMapCapability(x.getObject()));
        });
    }
}
