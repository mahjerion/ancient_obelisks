package com.robertx22.ancient_obelisks.affix.types;

import com.robertx22.ancient_obelisks.database.ObeliskDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.LazyClass;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class AttributeObeliskAffix extends BaseObeliskAffix implements JsonExileRegistry<AttributeObeliskAffix>, IAutoGson<AttributeObeliskAffix> {
    public static AttributeObeliskAffix SERIALIZER = new AttributeObeliskAffix(Affects.MOB, "", 0, "", "", AttributeModifier.Operation.ADDITION, 1);


    public String attribute_id = "";
    public String uuid = "";
    public AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;
    public double number = 0;

    public transient LazyClass<UUID> lazyUUID = new LazyClass<>(() -> UUID.fromString(uuid));
    public transient LazyClass<Attribute> lazyAttribute = new LazyClass<>(() -> BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attribute_id)));

    public AttributeObeliskAffix(Affects affects, String id, int weight, String attribute_id, String uuid, AttributeModifier.Operation operation, double number) {
        super(affects, id, weight);
        this.attribute_id = attribute_id;
        this.uuid = uuid;
        this.operation = operation;
        this.number = number;
    }


    @Override
    void applyINTERNAL(LivingEntity en) {

        AttributeModifier mod = new AttributeModifier(
                lazyUUID.get(),
                attribute_id,
                number,
                operation
        );

        AttributeInstance atri = en.getAttribute(lazyAttribute.get());

        if (atri != null) {
            if (atri.hasModifier(mod)) {
                atri.removeModifier(mod);
            }
            atri.addTransientModifier(mod);
        }

    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return ObeliskDatabase.ATTRIBUTE_AFFIX;
    }

    @Override
    public Class<AttributeObeliskAffix> getClassForSerialization() {
        return AttributeObeliskAffix.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }
}
