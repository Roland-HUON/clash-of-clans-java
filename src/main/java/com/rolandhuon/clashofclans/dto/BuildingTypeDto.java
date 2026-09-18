package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.building.BuildingType;

public record BuildingTypeDto(String id, String label, int maxCount, int maxLevel,
                              boolean defensive, boolean resource, boolean camp,
                              String targets, String targeting, String attackType, int splashBystanders,
                              String upgradeResource, int buildCost) {

    public static BuildingTypeDto from(BuildingType type){
        return new BuildingTypeDto(type.name(), type.label(), type.maxCount(), type.maxLevel(),
                type.isDefensive(), type.isResourceBuilding(), type.storesTroops(),
                type.targetScope().name(), type.targetingMode().name(),
                type.attackProfile().type().name(), type.attackProfile().splashTargets(),
                type.upgradeResource().name(), type.buildCost());
    }
}
