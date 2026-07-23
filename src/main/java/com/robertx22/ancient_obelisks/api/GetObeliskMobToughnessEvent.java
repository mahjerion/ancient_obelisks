package com.robertx22.ancient_obelisks.api;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

// Synchronous query, same reasoning as GetObeliskChestBonusEvent: ancient_obelisks can't see the main
// mod's player Stat pipeline, so it asks for the Atlas "Greater Trial" mob toughness bonus (percent,
// highest among the players present) to scale ObeliskMobTierStats' HP/damage multipliers.
public class GetObeliskMobToughnessEvent extends ExileEvent {

    public final List<Player> players;
    public float bonusPercent = 0;

    public GetObeliskMobToughnessEvent(List<Player> players) {
        this.players = players;
    }
}
