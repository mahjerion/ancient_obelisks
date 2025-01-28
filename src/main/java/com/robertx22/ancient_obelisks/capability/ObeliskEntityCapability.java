package com.robertx22.ancient_obelisks.capability;

import com.google.gson.JsonSyntaxException;
import com.robertx22.ancient_obelisks.main.ObelisksMain;
import com.robertx22.library_of_exile.registry.IAutoGson;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObeliskEntityCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public LivingEntity en;

    public static final ResourceLocation RESOURCE = new ResourceLocation(ObelisksMain.MODID, "entity");
    public static Capability<ObeliskEntityCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    transient final LazyOptional<ObeliskEntityCapability> supp = LazyOptional.of(() -> this);

    public ObeliskEntityCapability(LivingEntity en) {
        this.en = en;
    }

    public static ObeliskEntityCapability get(LivingEntity entity) {
        return entity.getServer().overworld().getCapability(INSTANCE).orElse(new ObeliskEntityCapability(entity));
    }

    public ObeliskEntityData data = new ObeliskEntityData();

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
            nbt.putString("data", IAutoGson.GSON.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        try {
            if (nbt.contains("data")) {
                this.data = IAutoGson.GSON.fromJson(nbt.getString("data"), ObeliskEntityData.class);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }
}
