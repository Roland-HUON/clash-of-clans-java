package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.model.Village;

import java.util.List;

public record VillageDto(Long id, String name, Long playerId,
                         int gold, int elixir, int darkElixir,
                         List<VillageBuildingDto> buildings) {

    public static VillageDto from(Village village) {
        return new VillageDto(
                village.getId(),
                village.getName(),
                village.getPlayer().getId(),
                village.getGold(),
                village.getElixir(),
                village.getDarkElixir(),
                village.getBuildings().stream().map(VillageBuildingDto::from).toList());
    }
}
