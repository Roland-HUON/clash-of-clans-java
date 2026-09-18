package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.village.Harvest;
import com.rolandhuon.clashofclans.model.Village;

import java.util.List;

public record VillageDto(Long id, String name, Long playerId,
                         long gold, long elixir, long darkElixir,
                         HarvestDto pending,
                         List<VillageBuildingDto> buildings) {

    public static VillageDto from(Village village) {
        return from(village, Harvest.nothing());
    }

    public static VillageDto from(Village village, Harvest pending) {
        return new VillageDto(
                village.getId(),
                village.getName(),
                village.getPlayer().getId(),
                village.getGold(),
                village.getElixir(),
                village.getDarkElixir(),
                HarvestDto.from(pending),
                village.getBuildings().stream().map(VillageBuildingDto::from).toList());
    }
}
