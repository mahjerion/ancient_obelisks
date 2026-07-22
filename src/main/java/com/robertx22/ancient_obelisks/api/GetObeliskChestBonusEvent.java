package com.robertx22.ancient_obelisks.api;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

// Synchronous query, same reasoning as dungeon_realm's GetPackSizeBonusEvent: ancient_obelisks can't
// see the main mod's player Stat pipeline, so it asks for the Atlas "Obelisk Extra Drops" bonus
// (percent, highest among the players present) to scale the reward-chest count in
// ObeliskRewardLogic.spawnChests - the player-stat parallel to the TRIPLE_CHEST_REWARD_CHANCE relic stat.
public class GetObeliskChestBonusEvent extends ExileEvent {

    public final List<Player> players;
    public float bonusPercent = 0;

    public GetObeliskChestBonusEvent(List<Player> players) {
        this.players = players;
    }
}
