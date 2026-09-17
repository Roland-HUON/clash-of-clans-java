package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.building.BuildingType;

public record BuildingTypeDto(String id, String label, int maxCount, int maxLevel) {
    public static BuildingTypeDto from(BuildingType type){
        return new BuildingTypeDto(type.name(), type.label(), type.maxCount(), type.maxLevel());
    }
}
