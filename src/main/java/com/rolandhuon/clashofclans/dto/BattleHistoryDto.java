package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.model.BattleRecord;

import java.time.Instant;

public record BattleHistoryDto(Long id, String kind, Instant playedAt, int destructionPercentage, int stars, int turns, int survivingTroops) {
    public static BattleHistoryDto from(BattleRecord record){
        return new BattleHistoryDto(record.getId(), record.getKind().name(), record.getPlayedAt(), record.getDestructionPercentage(), record.getStars(), record.getTurns(), record.getSurvivingTroops());
    }
}
