package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.model.PlayerTroop;

public record PlayerTroopDto(Long id, String type, String label, int level, int maxLevel,
                             int hitPoints, int damage, Integer nextUpgradeCost) {

    public static PlayerTroopDto from(PlayerTroop troop) {
        TroopType type = troop.getType();
        int level = troop.getLevel();

        Integer nextCost = (level < type.maxLevel()) ? type.upgradeCostFrom(level) : null;

        return new PlayerTroopDto(
                troop.getId(),
                type.name(),
                type.label(),
                level,
                type.maxLevel(),
                type.statsAt(level).hp(),
                type.statsAt(level).dps(),
                nextCost);
    }
}
