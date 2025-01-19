package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.block_entity.ObeliskBE;
import com.robertx22.ancient_obelisks.block_entity.ObeliskBlock;
import com.robertx22.ancient_obelisks.block_entity.ObeliskMobSpawnerBE;
import com.robertx22.ancient_obelisks.block_entity.ObeliskMobSpawnerBlock;
import com.robertx22.ancient_obelisks.item.HomeItem;
import com.robertx22.ancient_obelisks.item.ObeliskMapItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ObeliskEntries {
    // registars
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ObelisksMain.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ObelisksMain.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ObelisksMain.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ObelisksMain.MODID);

    // blocks
    public static RegistryObject<ObeliskMobSpawnerBlock> SPAWNER_BLOCK = BLOCKS.register("obelisk_spawner", () -> new ObeliskMobSpawnerBlock());
    public static RegistryObject<ObeliskBlock> OBELISK_BLOCK = BLOCKS.register("obelisk", () -> new ObeliskBlock());

    // block entities
    public static RegistryObject<BlockEntityType<ObeliskMobSpawnerBE>> SPAWNER_BE = BLOCK_ENTITIES.register("obelisk_spawner", () -> BlockEntityType.Builder.of(ObeliskMobSpawnerBE::new, SPAWNER_BLOCK.get()).build(null));
    public static RegistryObject<BlockEntityType<ObeliskBE>> OBELISK_BE = BLOCK_ENTITIES.register("obelisk", () -> BlockEntityType.Builder.of(ObeliskBE::new, OBELISK_BLOCK.get()).build(null));


    // items
    public static RegistryObject<BlockItem> OBELISK_ITEM = ITEMS.register("obelisk", () -> new BlockItem(OBELISK_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static RegistryObject<ObeliskMapItem> OBELISK_MAP_ITEM = ITEMS.register("obelisk_map", () -> new ObeliskMapItem());
    public static RegistryObject<HomeItem> HOME_TP_BACK = ITEMS.register("home", () -> new HomeItem());

    public static void initDeferred() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

    public static void init() {

    }
}
