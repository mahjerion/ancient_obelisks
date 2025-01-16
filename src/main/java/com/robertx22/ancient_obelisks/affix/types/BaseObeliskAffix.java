package com.robertx22.ancient_obelisks.affix.types;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class BaseObeliskAffix {

    public BaseObeliskAffix(Affects affects, String id, int weight) {
        this.affects = affects;
        this.id = id;
        this.weight = weight;
    }

    public enum Affects {
        PLAYER() {
            @Override
            public boolean is(LivingEntity en) {
                return en instanceof Player;
            }
        }, MOB() {
            @Override
            public boolean is(LivingEntity en) {
                return en instanceof Player == false;
            }
        };

        public abstract boolean is(LivingEntity en);
    }

    public Affects affects = Affects.MOB;
    public String id = "";
    public int weight = 1000;


    public void apply(LivingEntity en) {
        if (affects.is(en)) {
            applyINTERNAL(en);
        }
    }

    abstract void applyINTERNAL(LivingEntity en);


}
