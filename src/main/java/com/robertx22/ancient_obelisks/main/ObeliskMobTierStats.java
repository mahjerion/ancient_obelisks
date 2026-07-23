package com.robertx22.ancient_obelisks.main;

import com.robertx22.ancient_obelisks.api.GetObeliskMobToughnessEvent;
import com.robertx22.ancient_obelisks.api.ObeliskExileEvents;
import com.robertx22.ancient_obelisks.configs.ObeliskConfig;
import com.robertx22.ancient_obelisks.structure.ObeliskMapData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class ObeliskMobTierStats {

    public static UUID DMG = UUID.fromString("012d5bd9-4917-4747-b188-1f8da6871133");
    public static UUID HP = UUID.fromString("19719d5f-e800-468b-839c-b907f9ab89ab");

    // Atlas "Greater Trial" - separate modifiers layered on top of the base tier scaling below
    public static UUID TOUGHNESS_DMG = UUID.fromString("2b3c4d5e-6f70-4a1b-9c8d-7e6f5a4b3c2d");
    public static UUID TOUGHNESS_HP = UUID.fromString("3c4d5e6f-708a-4b1c-8d9e-6f5a4b3c2d1e");


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

                // Atlas "Greater Trial" - additional HP/damage on top of the base tier scaling above,
                // scaled by the party-max toughness bonus (queried once, at spawn time)
                if (!attributes.hasModifier(Attributes.MAX_HEALTH, TOUGHNESS_HP) && !attributes.hasModifier(Attributes.ATTACK_DAMAGE, TOUGHNESS_DMG)) {
                    float toughnessBonus = getToughnessBonus(en);
                    if (toughnessBonus > 0) {
                        if (maxHealthAttribute != null) {
                            maxHealthAttribute.addPermanentModifier(new AttributeModifier(
                                    TOUGHNESS_HP, "Obelisk Greater Trial",
                                    toughnessBonus / 100F, AttributeModifier.Operation.MULTIPLY_TOTAL));
                            en.setHealth((int) maxHealthAttribute.getValue());
                        }
                        if (attackDamageAttribute != null) {
                            attackDamageAttribute.addPermanentModifier(new AttributeModifier(
                                    TOUGHNESS_DMG, "Obelisk Greater Trial",
                                    toughnessBonus / 100F, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static float getToughnessBonus(LivingEntity en) {
        List<Player> players = ObelisksMain.OBELISK_MAP_STRUCTURE.getAllPlayersInMap(en.level(), en.blockPosition());
        return ObeliskExileEvents.GET_MOB_TOUGHNESS_BONUS.callEvents(new GetObeliskMobToughnessEvent(players)).bonusPercent;
    }
}
