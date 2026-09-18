package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.service.RaidOutcome;

public record RaidResultDto(int destructionPercentage, int stars, int turns,
                            int survivingTroops, boolean villageDestroyed,
                            long lootedGold, long lootedElixir, long lootedDarkElixir,
                            int trophyChange, int attackerTrophies, int defenderTrophies) {

    public static RaidResultDto from(RaidOutcome outcome) {
        return new RaidResultDto(
                outcome.battle().destructionPercentage(),
                outcome.battle().stars(),
                outcome.battle().turns(),
                outcome.battle().survivingTroops(),
                outcome.battle().villageDestroyed(),
                outcome.loot().gold(),
                outcome.loot().elixir(),
                outcome.loot().darkElixir(),
                outcome.trophies().attackerDelta(),
                outcome.attackerTrophies(),
                outcome.defenderTrophies());
    }
}
