package com.rolandhuon.clashofclans.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PlayerRequest(@NotBlank @Size(max = 40) String name,
                            @Min(1) int level,
                            @PositiveOrZero long gold,
                            @PositiveOrZero long elixir,
                            @PositiveOrZero long darkElixir) {
}
