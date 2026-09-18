package com.rolandhuon.clashofclans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record VillageRequest(@NotBlank @Size(max = 60) String name,
                             @PositiveOrZero long gold,
                             @PositiveOrZero long elixir,
                             @PositiveOrZero long darkElixir) {
}
