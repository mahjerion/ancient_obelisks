package com.robertx22.ancient_obelisks.structure;

import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObeliskMapCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public Level world;

    public static final ResourceLocation RESOURCE = new ResourceLocation(ObelisksMain.MODID, "world_data");
    public static Capability<ObeliskMapCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<ObeliskMapCapability> supp = LazyOptional.of(() -> this);

    public ObeliskMapCapability(Level world) {
        this.world = world;
    }

    public static ObeliskMapCapability get(Level entity) {
        return entity.getServer().overworld().getCapability(INSTANCE).orElse(new ObeliskMapCapability(entity));
    }


    public ObeliskWorldData data = new ObeliskWorldData();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == INSTANCE) {
            return supp.cast();
        }
        return LazyOptional.empty();

    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();

        try {
            LoadSave.Save(data, nbt, "data");

        } catch (Exception e) {
            e.printStackTrace();
        }


        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {

            this.data = LoadSave.loadOrBlank(ObeliskWorldData.class, new ObeliskWorldData(), nbt, "data", new ObeliskWorldData());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
