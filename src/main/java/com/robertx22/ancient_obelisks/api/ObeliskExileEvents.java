package com.robertx22.ancient_obelisks.api;

import com.robertx22.library_of_exile.events.base.ExileEventCaller;

public class ObeliskExileEvents {

    public static ExileEventCaller<GetObeliskChestBonusEvent> GET_CHEST_REWARD_BONUS = new ExileEventCaller<>();
    public static ExileEventCaller<GetObeliskMobToughnessEvent> GET_MOB_TOUGHNESS_BONUS = new ExileEventCaller<>();

    public static void init() {

    }
}
