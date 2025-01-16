package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.block.ObeliskTpBackBlock;
import com.robertx22.ancient_obelisks.block_entity.ObeliskBE;
import com.robertx22.ancient_obelisks.block_entity.ObeliskBlock;
import com.robertx22.ancient_obelisks.block_entity.ObeliskMobSpawnerBE;
import com.robertx22.ancient_obelisks.block_entity.ObeliskMobSpawnerBlock;
import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.ancient_obelisks.item.HomeItem;
import com.robertx22.ancient_obelisks.item.ObeliskMapItem;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.ancient_obelisks.structure.ObeliskMapStructure;
import com.robertx22.library_of_exile.dimension.MapChunkGenEvent;
import com.robertx22.library_of_exile.dimension.MapChunkGens;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

@Mod("ancient_obelisks")
public class ObelisksMain {
    public static boolean RUN_DEV_TOOLS = true;

    public static String ID = "ancient_obelisks";
    public static String DIMENSION_ID = "ancient_obelisks:obelisk";
    public static ResourceLocation DIMENSION_KEY = new ResourceLocation(DIMENSION_ID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(ID, id);
    }

    // registars
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);

    // blocks
    public static RegistryObject<ObeliskMobSpawnerBlock> SPAWNER_BLOCK = BLOCKS.register("obelisk_spawner", () -> new ObeliskMobSpawnerBlock());
    public static RegistryObject<ObeliskBlock> OBELISK_BLOCK = BLOCKS.register("obelisk", () -> new ObeliskBlock());
    public static RegistryObject<ObeliskTpBackBlock> TELEPORT_BACK_BLOCK = BLOCKS.register("obelisk_teleport_back", () -> new ObeliskTpBackBlock());

    // block entities
    public static RegistryObject<BlockEntityType<ObeliskMobSpawnerBE>> SPAWNER_BE = BLOCK_ENTITIES.register("obelisk_spawner", () -> BlockEntityType.Builder.of(ObeliskMobSpawnerBE::new, SPAWNER_BLOCK.get()).build(null));
    public static RegistryObject<BlockEntityType<ObeliskBE>> OBELISK_BE = BLOCK_ENTITIES.register("obelisk", () -> BlockEntityType.Builder.of(ObeliskBE::new, OBELISK_BLOCK.get()).build(null));


    // items
    public static RegistryObject<BlockItem> OBELISK_ITEM = ITEMS.register("obelisk", () -> new BlockItem(OBELISK_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static RegistryObject<BlockItem> TELEPORT_BACK_BLOCK_ITEM = ITEMS.register("obelisk_teleport_back", () -> new BlockItem(TELEPORT_BACK_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static RegistryObject<ObeliskMapItem> OBELISK_MAP_ITEM = ITEMS.register("obelisk_map", () -> new ObeliskMapItem());
    public static RegistryObject<HomeItem> HOME_TP_BACK = ITEMS.register("home", () -> new HomeItem());


    // other
    public static ObeliskMapStructure OBELISK_MAP_STRUCTURE = new ObeliskMapStructure();

    public ObelisksMain() {

        if (RUN_DEV_TOOLS) {
            ExileRegistryUtil.setCurrentRegistarMod(ObelisksMain.ID);
        }

        MapDimensions.map.put(DIMENSION_ID, true);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ObeliskConfig.SPEC);
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);

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

        ApiForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (RUN_DEV_TOOLS) {
                ObeliskDatabase.generateJsons();
            }
        });


        CREATIVE_TAB.register("ancient_obelisk_tab", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 2)
                .icon(() -> OBELISK_ITEM.get().getDefaultInstance())
                .title(Component.translatable("ancient_obelisks.name").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                .displayItems(new CreativeModeTab.DisplayItemsGenerator() {
                    @Override
                    public void accept(CreativeModeTab.ItemDisplayParameters param, CreativeModeTab.Output output) {
                        output.accept(OBELISK_ITEM.get());
                        output.accept(OBELISK_MAP_ITEM.get());
                        output.accept(HOME_TP_BACK.get());
                        output.accept(TELEPORT_BACK_BLOCK_ITEM.get());
                    }
                })
                .build());


        ObeliskDatabase.initRegistries();


        System.out.println("Ancient Obelisks loaded.");
    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {

        ObeliskDatabase.registerObjects();

        ComponentInit.reg();

        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, (Consumer<AttachCapabilitiesEvent<Level>>) x -> {
            x.addCapability(ObeliskMapCapability.RESOURCE, new ObeliskMapCapability(x.getObject()));
        });
    }
}
