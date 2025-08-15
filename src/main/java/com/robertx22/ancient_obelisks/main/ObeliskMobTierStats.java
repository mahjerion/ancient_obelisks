package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.structure.ObeliskMapData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class ObeliskMobTierStats {

    public static UUID DMG = UUID.fromString("012d5bd9-4917-4747-b188-1f8da6871133");
    public static UUID HP = UUID.fromString("19719d5f-e800-468b-839c-b907f9ab89ab");


    public static AttributeModifier hpMod(int tier) {
        float hp = ObeliskConfig.get().MOB_HP_PER_TIER.get().floatValue() * tier;

        AttributeModifier mod = new AttributeModifier(
                HP,
                Attributes.MAX_HEALTH.getDescriptionId(),
                hp,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        return mod;
    }

    public static AttributeModifier dmgMod(int tier) {
        float dmg = ObeliskConfig.get().MOB_DMG_PER_TIER.get().floatValue() * tier;

        AttributeModifier mod = new AttributeModifier(
                DMG,
                Attributes.ATTACK_DAMAGE.getDescriptionId(),
                dmg,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        return mod;
    }


    public static void tryApply(LivingEntity en, ObeliskMapData data) {

        try {
            int tier = data.item.tier;

            if (tier > 0) {
                var attributes = en.getAttributes();

                AttributeInstance maxHealthAttribute = en.getAttribute(Attributes.MAX_HEALTH);
                if (!attributes.hasModifier(Attributes.MAX_HEALTH, HP) && maxHealthAttribute != null) {
                    maxHealthAttribute.addPermanentModifier(hpMod(tier));
                    en.setHealth((int) maxHealthAttribute.getValue());
                }

                AttributeInstance attackDamageAttribute = en.getAttribute(Attributes.ATTACK_DAMAGE);
                if (!attributes.hasModifier(Attributes.ATTACK_DAMAGE, DMG) && attackDamageAttribute != null) {
                    attackDamageAttribute.addPermanentModifier(dmgMod(tier));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
