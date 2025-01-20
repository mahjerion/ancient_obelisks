package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.ancient_obelisks.database.holders.ObeliskItemMods;
import com.robertx22.ancient_obelisks.database.holders.ObeliskItemReqs;
import com.robertx22.ancient_obelisks.database.holders.ObeliskMapBlocks;
import com.robertx22.ancient_obelisks.database.holders.ObeliskOrbs;
import com.robertx22.library_of_exile.registry.ExileRegistryEventClass;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.OrderedModConstructor;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Arrays;
import java.util.List;

public class ObeliskModConstructor extends OrderedModConstructor {
    public ObeliskModConstructor(String modid, IEventBus modbus) {
        super(modid, modbus);
    }

    @Override
    public List<ExileRegistryEventClass> getRegisterEvents() {
        return Arrays.asList(

        );
    }

    @Override
    public List<ExileKeyHolder> getAllKeyHolders() {
        return Arrays.asList(
                ObeliskMapBlocks.INSTANCE,
                ObeliskItemMods.INSTANCE,
                ObeliskItemReqs.INSTANCE,
                ObeliskOrbs.INSTANCE
        );
    }

    @Override
    public void registerDeferredContainers(IEventBus iEventBus) {
        ObeliskEntries.initDeferred();
    }

    @Override
    public void registerDeferredEntries() {
        ObeliskEntries.init();
    }

    @Override
    public void registerDatabases() {
        ObeliskDatabase.initRegistries();
    }

    @Override
    public void registerDatabaseEntries() {
        ObeliskDatabase.registerObjects();
    }
}
