package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.capability.ObeliskEntityCapability;
import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.ancient_obelisks.structure.ObeliskMapCapability;
import com.robertx22.ancient_obelisks.structure.ObeliskMapData;
import com.robertx22.ancient_obelisks.structure.ObeliskMapStructure;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfigDefaults;
import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.affix.base.GrabMobAffixesEvent;
import com.robertx22.library_of_exile.dimension.MapChunkGenEvent;
import com.robertx22.library_of_exile.dimension.MapChunkGens;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.library_of_exile.utils.SoundUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Mod("ancient_obelisks")
public class ObelisksMain {
    public static boolean RUN_DEV_TOOLS = false;

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

        ApiForgeEvents.registerForgeEvent(GatherDataEvent.class, event -> {
            var output = event.getGenerator().getPackOutput();
            var chestsLootTables = new LootTableProvider.SubProviderEntry(ObeliskLootTables.ObeliskLootTableProvider::new, LootContextParamSets.CHEST);
            var provider = new LootTableProvider(output, Set.of(), List.of(chestsLootTables));
            event.getGenerator().addProvider(true, provider);

            if (RUN_DEV_TOOLS) {
                // todo this doesnt seem to gen here?   ObeliskDatabase.generateJsons();
            }

            try {
                // .. why does this not work otherwise?
                event.getGenerator().run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


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

        ExileEvents.GRAB_MOB_AFFIXES.register(new EventConsumer<GrabMobAffixesEvent>() {
            @Override
            public void accept(GrabMobAffixesEvent e) {
                var map = MapDimensions.getInfo(e.en.level());
                if (map != null && map.dimensionId.equals(DIMENSION_KEY)) {
                    var mapdata = ObeliskMapCapability.get(e.en.level()).data.data.getData(OBELISK_MAP_STRUCTURE, e.en.blockPosition());
                    if (mapdata != null) {
                        for (int i = 0; i < mapdata.currentWave; i++) {
                            for (ExileAffixData affix : mapdata.item.getAffixesForWave(i)) {
                                if (affix.getAffix().affects.is(e.en)) {
                                    e.allAffixes.add(affix);
                                }
                            }
                        }
                    }
                }
            }
        });

        ApiForgeEvents.registerForgeEvent(LivingEvent.LivingTickEvent.class, event -> {
            var en = event.getEntity();
            if (!en.level().isClientSide) {
                if (en.tickCount == 2) {
                    ifMapData(event.getEntity().level(), event.getEntity().blockPosition()).ifPresent(x -> {
                        ObeliskMobTierStats.tryApply(en, x);
                    });
                }
            }
        });


        ObeliskEntries.CREATIVE_TAB.register(MODID, () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 2)
                .icon(() -> ObeliskEntries.OBELISK_ITEM.get().getDefaultInstance())
                .title(ObeliskWords.CREATIVE_TAB.get().withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                .displayItems(new CreativeModeTab.DisplayItemsGenerator() {
                    @Override
                    public void accept(CreativeModeTab.ItemDisplayParameters param, CreativeModeTab.Output output) {
                        for (Item item : ForgeRegistries.ITEMS) {
                            if (ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(ObelisksMain.MODID)) {
                                output.accept(item);
                            }
                        }
                    }
                })
                .build());


        ObeliskDatabase.initRegistries();
        ObeliskCommands.init();
        ObeliskRewardLogic.init();

        ExileEvents.ON_CHEST_LOOTED.register(new EventConsumer<ExileEvents.OnChestLooted>() {
            @Override
            public void accept(ExileEvents.OnChestLooted e) {
                try {
                    float chance = (float) (ObeliskConfig.get().OBELISK_SPAWN_CHANCE_ON_CHEST_LOOT.get() * ObeliskConfig.get().getDimChanceMulti(e.player.level()));
                    if (RandomUtils.roll(chance)) {
                        if (!MapDimensions.isMap(e.player.level())) {
                            var pos = ObeliskRewardLogic.findNearbyFreeChestPos(e.player.level(), e.pos, x -> !x.isAir() && !x.hasBlockEntity(), 2);
                            if (pos != null) {
                                SoundUtils.playSound(e.player, SoundEvents.EXPERIENCE_ORB_PICKUP);
                                e.player.level().setBlock(pos, ObeliskEntries.OBELISK_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                            }

                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        System.out.println("Ancient Obelisks loaded.");

    }

    public static Optional<ObeliskMapData> ifMapData(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return Optional.empty();
        }
        var map = MapDimensions.getInfo(level);
        if (map != null && map.dimensionId.equals(DIMENSION_KEY)) {
            var mapdata = ObeliskMapCapability.get(level).data.data.getData(OBELISK_MAP_STRUCTURE, pos);
            if (mapdata != null) {
                return Optional.of(mapdata);
            }
        }
        return Optional.empty();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ObeliskClient.init();
    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {


        ComponentInit.reg();

        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, (Consumer<AttachCapabilitiesEvent<Level>>) x -> {
            x.addCapability(ObeliskMapCapability.RESOURCE, new ObeliskMapCapability(x.getObject()));
        });

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, (Consumer<AttachCapabilitiesEvent<Entity>>) x -> {
            if (x.getObject() instanceof LivingEntity en) {
                x.addCapability(ObeliskEntityCapability.RESOURCE, new ObeliskEntityCapability(en));
            }
        });

    }
}
