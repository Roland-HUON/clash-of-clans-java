package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.model.VillageBuilding;

public record VillageBuildingDto(Long id, String type, String label, int level, int maxLevel,
                                 int hitPoints, int housingCapacity,
                                 String upgradeResource, Integer nextUpgradeCost) {

    public static VillageBuildingDto from(VillageBuilding building) {
        BuildingType type = building.getType();
        int level = building.getLevel();

        Integer nextCost = (level < type.maxLevel()) ? type.upgradeCostFrom(level) : null;

        return new VillageBuildingDto(
                building.getId(),
                type.name(),
                type.label(),
                level,
                type.maxLevel(),
                type.hpAt(level),
                type.housingCapacityAt(level),
                type.upgradeResource().name(),
                nextCost);
    }
}
