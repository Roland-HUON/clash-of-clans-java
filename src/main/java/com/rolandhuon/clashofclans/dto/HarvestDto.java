package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.village.Harvest;

public record HarvestDto(long gold, long elixir, long darkElixir) {

    public static HarvestDto from(Harvest harvest) {
        return new HarvestDto(harvest.gold(), harvest.elixir(), harvest.darkElixir());
    }
}
