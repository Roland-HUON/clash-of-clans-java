package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.troop.TroopType;

public record TroopTypeDto(String id, String label, int maxLevel, int housingSpace, double range,
                           String attackType, String movement, String role, String targeting) {

    public static TroopTypeDto from(TroopType type){
        var range = type.attackProfile().range();
        var attackType = type.attackProfile().type().name();
        return new TroopTypeDto(type.name(), type.label(), type.maxLevel(), type.housingSpace(), range,
                attackType, type.movement().name(), type.role().name(), type.targetingMode().name());
    }
}
