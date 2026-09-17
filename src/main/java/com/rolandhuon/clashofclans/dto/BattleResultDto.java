package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;

public record BattleResultDto(int destructionPercentage, int stars, int turns, int survivingTroops, boolean villageDestroyed) {
    public static BattleResultDto from(BattleResult result){
        return new BattleResultDto(result.destructionPercentage(), result.stars(), result.turns(), result.survivingTroops(), result.villageDestroyed());
    }
}
